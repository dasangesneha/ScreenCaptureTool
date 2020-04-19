import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class GeneratePpt {
	public static void generatePpt(File tempDirectory, String outputFolderPath)
			throws FileNotFoundException, IOException {
		File[] files = null;
		if (tempDirectory.exists() && tempDirectory.isDirectory()) {
			files = tempDirectory.listFiles();

			try (XMLSlideShow ppt = new XMLSlideShow()) {
				createSlides(ppt, files);
				try (FileOutputStream out = new FileOutputStream(outputFolderPath)) {
					ppt.write(out);
				}
			}
		} else {
			System.out.print("Something went wrong");
		}
	}

	public static void createSlides(XMLSlideShow ppt, File[] files) {
		try {
			for (File file : files) {
				XSLFSlide slide = ppt.createSlide();
				slide.createPicture(ppt.addPicture(file, PictureType.PNG));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
