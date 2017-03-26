package grafika.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photo")
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoController {

	private File currentFile;
	private byte[] currentImage;
	private int offset = 46;

	@RequestMapping("/upload")
	public String upload(@RequestParam MultipartFile file) throws IllegalStateException, IOException {
		String filename = file.getOriginalFilename();
		String userDir = System.getProperty("user.dir");
		userDir = userDir.replace('\\', '/');
		String path = userDir + "/src/main/resources/static/bmp/" + filename;
		// String path = "C:/Users/piotr/Desktop/grafika
		// projekt/ui/grafika/src/assets/bmp/" + filename;
		File savedFile = new File(path);
		currentFile = savedFile;
		file.transferTo(savedFile);
		this.currentImage = file.getBytes();
		return savedFile.getAbsolutePath();
	}

	@RequestMapping("/filter")
	public void filter() throws IOException {
		BufferedImage buf = ImageIO.read(currentFile);
		int imageWidth = buf.getWidth();
		int imageHeight = buf.getHeight();
		for (int i = 0; i < imageWidth / 2; i++) {
			for (int j = 0; j < imageWidth / 2; j++) {
				buf.setRGB(i, j, 0);
			}
		}

		System.out.println("/filter");
		// System.out.println(Arrays.toString(this.currentImage));
		// System.out.println(this.currentImage.length);
		// int alphaCounter = 1;
		// for (int i = offset; i < currentImage.length; i++) {
		// alphaCounter++;
		// if (alphaCounter == 4) {
		// alphaCounter = 1;
		// continue;
		// }
		// currentImage[i] = (byte) (i % 255);
		// }
		ImageIO.write(buf, "bmp", currentFile);
		// FileOutputStream fos = new
		// FileOutputStream(currentFile.getAbsolutePath());
		// fos.write(currentImage);
		// fos.close();

	}

	@RequestMapping("/test")
	public String test() {
		return "test";
	}

}
