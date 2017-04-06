package grafika.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	private String currentFilename;

	@RequestMapping("/upload")
	public String upload(@RequestParam MultipartFile file) throws IllegalStateException, IOException {
		currentFilename = file.getOriginalFilename();
		String userDir = System.getProperty("user.dir");
		userDir = userDir.replace('\\', '/');
		String backendPath = userDir + "/src/main/resources/static/bmp/" + currentFilename;
		String frontendPath = "C:/Users/piotr/Desktop/grafika projekt/ui/grafika/src/assets/bmp/" + currentFilename;
		File uiFile = new File(frontendPath);
		File savedFile = new File(backendPath);
		currentFile = savedFile;
		file.transferTo(savedFile);
		file.transferTo(uiFile);
		currentImage = file.getBytes();
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
		ImageIO.write(buf, "bmp", currentFile);
		String path = "C:/Users/piotr/Desktop/grafika projekt/ui/grafika/src/assets/bmp/" + currentFilename;
		File uiFile = new File(path);
		ImageIO.write(buf, "bmp", uiFile);
	}

	@RequestMapping("/test")
	public String test() {
		return "test";
	}

}
