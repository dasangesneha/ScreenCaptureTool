import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class Launcher {
	private final static String tempDirectoryPath = "C:\\screenshots";
	private static File tempSubDirectory;
	private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH.mm.ss");

	public static void main(String[] args) throws AWTException {
		registerNativeHook();
		createTempDirectories();

		new GuiForm(tempSubDirectory).createFrame();

		new GlobalKeyListener(new Robot(), tempSubDirectory);
	}

	public static void registerNativeHook() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}

	public static void createTempDirectories() {
		final File tempDirectory = new File(tempDirectoryPath);
		if (!tempDirectory.exists()) {
			tempDirectory.mkdir();
		}
		tempSubDirectory = new File(tempDirectoryPath + "\\" + dateTimeFormat.format(LocalDateTime.now()));
		if (!tempSubDirectory.exists()) {
			tempSubDirectory.mkdir();
		}
	}
}
