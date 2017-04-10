package grafika.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import grafika.services.FiltersService;

@RestController
@RequestMapping("/photo")
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoController {

	@Autowired
	private FiltersService filtersService;
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
	public ResponseEntity<?> filter() throws IOException {
		BufferedImage buf = ImageIO.read(currentFile);
		buf = filtersService.filter(buf, FiltersService.gaussianBlur);
		ImageIO.write(buf, "bmp", currentFile);
		String path = "C:/Users/piotr/Desktop/grafika projekt/ui/grafika/src/assets/bmp/" + currentFilename;
		File uiFile = new File(path);
		ImageIO.write(buf, "bmp", uiFile);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/test")
	public String test() {
		return "test";
	}

}
