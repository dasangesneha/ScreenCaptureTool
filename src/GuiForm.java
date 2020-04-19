import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class GuiForm {
	private JFrame frame = new JFrame();
	private JPanel mainPanel = new JPanel();
	private String outputFolderPath;
	private File tempSubDirectory;
	private JTextField pptFileName;
	private JProgressBar progressBar;
	private JComboBox<String> extensionComboBox;
	private static int screenShotCount = 0;
	private static boolean isStopClicked = false;
	private static boolean isProcessInterrupted = false;

	GuiForm(File tempSubDirectory) {
		this.tempSubDirectory = tempSubDirectory;
	}

	public void createFrame() {
		mainPanel.setLayout(new GridLayout(5, 1));

		generateLabel();

		generateOutputFolderSelectionPanel();

		generateOutputFileNameAndScreenshotCountCopmonents();

		generateExportToPptComponent();

		generateStartStopButtons();

		showProgressBar();

		addClosingWindow();

		frame.setTitle("Screen Capture Tool");
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);
		frame.setSize(450, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void generateLabel() {
		JLabel label = new JLabel("Screen Capture");
		label.setHorizontalAlignment(JLabel.CENTER);
		frame.getContentPane().add(BorderLayout.NORTH, label);
	}

	public void generateOutputFolderSelectionPanel() {
		JPanel panel1 = new JPanel();
		panel1.add(new JLabel("Output Folder"));
		JTextField outputFolderPathTextField = new JTextField(20);
		panel1.add(outputFolderPathTextField);
		JButton selectFileButton = new JButton("Select");
		selectFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Please Select Output File Location !!!");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					outputFolderPath = chooser.getSelectedFile().toString();
					outputFolderPathTextField.setText(outputFolderPath);
				}

				if (outputFolderPath.equalsIgnoreCase("C:\\")) {
					JOptionPane.showMessageDialog(null, "Cannot generate any file directly into root folder !!", "",
							JOptionPane.WARNING_MESSAGE);
					outputFolderPathTextField.setText("");
				}
			}
		});
		panel1.add(selectFileButton);
		mainPanel.add(panel1);
	}

	public void generateOutputFileNameAndScreenshotCountCopmonents() {
		JPanel panel2 = new JPanel();
		panel2.add(new JLabel("Final Output File Name"));
		pptFileName = new JTextField(10);
		pptFileName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progressBar.setValue(0);
			}
		});
		panel2.add(pptFileName);
		panel2.add(new JLabel("Screen Shot Count"));
		JTextField screenShotCountTxtField = new JTextField(5);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				screenShotCountTxtField.setText(String.valueOf(screenShotCount));
			}
		}, 1000, 1000);
		panel2.add(screenShotCountTxtField);
		mainPanel.add(panel2);
	}

	public void generateExportToPptComponent() {
		JPanel panel3 = new JPanel();
		panel3.add(new JLabel("Export To"));
		String[] choices = { "pptx", "docx" };
		extensionComboBox = new JComboBox<String>(choices);
		extensionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				progressBar.setValue(0);
			}
		});
		panel3.add(extensionComboBox);
		mainPanel.add(panel3);
	}

	public void generateStartStopButtons() {
		JPanel panel4 = new JPanel();
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isStopClicked = false;
				progressBar.setValue(0);

				if (outputFolderPath == null || outputFolderPath.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please Enter Output Folder Location", "",
							JOptionPane.WARNING_MESSAGE);
				} else if (pptFileName.getText() == null || pptFileName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please Enter Output File Name", "",
							JOptionPane.WARNING_MESSAGE);
				} else if (tempSubDirectory.list() == null || tempSubDirectory.list().length == 0) {
					JOptionPane.showMessageDialog(null, "Cannot generate file as there are no screenshots captured !!",
							"", JOptionPane.WARNING_MESSAGE);
				} else {
					File dir = new File(outputFolderPath);

					if (extensionComboBox.getSelectedIndex() == 0) {
						// pptx

						File[] files = dir
								.listFiles((dir1, name) -> name.equalsIgnoreCase(pptFileName.getText() + ".pptx"));
						if (files.length > 0) {
							JOptionPane.showMessageDialog(null, "ppt with same name already exists in output folder !!",
									"", JOptionPane.WARNING_MESSAGE);
						} else {
							startThread(progressBar, tempSubDirectory,
									outputFolderPath + "\\" + pptFileName.getText() + ".pptx", ".pptx");
						}

					} else if (extensionComboBox.getSelectedIndex() == 1) {
						// docx

						File[] files = dir
								.listFiles((dir1, name) -> name.equalsIgnoreCase(pptFileName.getText() + ".docx"));
						if (files.length > 0) {
							JOptionPane.showMessageDialog(null, "doc with same name already exists in output folder !!",
									"", JOptionPane.WARNING_MESSAGE);
						} else {
							startThread(progressBar, tempSubDirectory,
									outputFolderPath + "\\" + pptFileName.getText() + ".docx", ".docx");
						}
					}
				}
			}
		});
		panel4.add(startButton);

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isStopClicked = true;
				progressBar.setValue(0);
			}
		});

//		new Timer().scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				if (isProcessInterrupted) {
//					String fileName = outputFolderPath + "\\" + pptFileName.getText();
//					File file;
//					fileName = (extensionComboBox.getSelectedIndex() == 0) ? fileName + ".pptx" : fileName + ".docx";
//					file = new File(fileName);
//					try {
//						file.delete();
//					} catch (Exception e) {
//						System.out.print("Exception : " + e);
//					}
//					isProcessInterrupted = false;
//				}
//			}
//		}, 1000, 1000);

		panel4.add(stopButton);
		mainPanel.add(panel4);
	}

	public void showProgressBar() {
		JPanel panel5 = new JPanel();
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		panel5.add(progressBar);
		mainPanel.add(panel5);
	}

	public void addClosingWindow() {
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to close this window?",
						"Exit Program Message Box", JOptionPane.YES_NO_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					System.exit(0);
				} else {
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
	}

	private static void startThread(JProgressBar progressBar, File fromDir, String fileName, String fileExtension) {
		SwingWorker sw1 = new SwingWorker() {

			@Override
			protected Object doInBackground() throws Exception {

				if (fileExtension.equals(".pptx")) {
					fillPptProgressBar(progressBar);
					GeneratePpt.generatePpt(fromDir, fileName);
				} else {
					fillDocProgressBar(progressBar);
					WordDocument.addImagesToWordDocument(fromDir, fileName);
				}
				return null;
			}
		};
		sw1.execute();
	}

	public static void fillPptProgressBar(JProgressBar progressBar) {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			int i = 0;

			@Override
			public void run() {
				while (i <= 1000) {
					i++;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!isStopClicked) {
						progressBar.setValue(i);
					} else {
						isProcessInterrupted = true;
						i = 2000;
					}
				}
			}
		}, 100, 100);
	}

	public static void fillDocProgressBar(JProgressBar progressBar) {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			int i = 0;

			@Override
			public void run() {
				while (i <= 100) {
					i++;
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!isStopClicked) {
						progressBar.setValue(i);
					} else {
						isProcessInterrupted = true;
						i = 2000;
					}
				}
			}
		}, 100, 100);
	}

	public void updateScreenShotCount(int screenShotCount) {
		this.screenShotCount = --screenShotCount;
	}
}
