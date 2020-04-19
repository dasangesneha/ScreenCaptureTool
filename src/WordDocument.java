import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class WordDocument {
	public static void addImagesToWordDocument(File tempDirectory, String outputFolderPath)
			throws IOException, InvalidFormatException {

		XWPFDocument doc = new XWPFDocument();
		XWPFParagraph p = doc.createParagraph();
		XWPFRun r = p.createRun();

		File[] files = null;
		if (tempDirectory.exists() && tempDirectory.isDirectory()) {
			files = tempDirectory.listFiles();

			for (File imageFile : files) {
				BufferedImage bufferedImage = ImageIO.read(imageFile);
				int width1 = bufferedImage.getWidth();
				int height1 = bufferedImage.getHeight();
				r.addPicture(new FileInputStream(imageFile), XWPFDocument.PICTURE_TYPE_PNG, imageFile.getName(),
						Units.toEMU(width1), Units.toEMU(height1));
				r.addBreak();
				r.addBreak();
			}

			FileOutputStream out = new FileOutputStream(outputFolderPath);
			doc.write(out);
			out.close();
			doc.close();
		} else {
			System.out.print("Something went wrong");
		}
	}
}