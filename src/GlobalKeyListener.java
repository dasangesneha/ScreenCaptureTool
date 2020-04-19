import javax.imageio.ImageIO;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

public class GlobalKeyListener implements NativeKeyListener {
	int num = 1;
	public static GlobalKeyListener instance;
	Robot robot;
	File folder;

	public GlobalKeyListener(Robot robot, File folder) {
		instance = this;
		GlobalScreen.addNativeKeyListener(this);
		this.robot = robot;
		this.folder = folder;
	}

	public int getScreenShotsCount() {
		return num;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_PRINTSCREEN) {
			try {
				Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
				BufferedImage screenshot = robot.createScreenCapture(screenRect);
				ImageIO.write(screenshot, "jpg", new File(folder + "/" + num + ".jpg"));
				num++;
				GuiForm g = new GuiForm(null);
				g.updateScreenShotCount(num);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}
}