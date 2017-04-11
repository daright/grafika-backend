package grafika.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	private File backendFile;
	private File frontendFile;
	private String backendFilename;

	@RequestMapping("/upload")
	public void upload(@RequestParam MultipartFile file) throws IllegalStateException, IOException {
		backendFilename = file.getOriginalFilename();
		String userDir = System.getProperty("user.dir");
		userDir = userDir.replace('\\', '/');
		String backendPath = userDir + "/src/main/resources/static/bmp/" + backendFilename;
		String frontendPath = "C:/Users/piotr/Desktop/grafika projekt/ui/grafika/src/assets/bmp/" + backendFilename;
		frontendFile = new File(frontendPath);
		backendFile = new File(backendPath);
		file.transferTo(backendFile);
		file.transferTo(frontendFile);
	}

	@RequestMapping("/box")
	public ResponseEntity<?> box() throws IOException {
		filterAndSave(FiltersService.boxFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/gaussianBlur")
	public ResponseEntity<?> gaussianBlur() throws IOException {
		filterAndSave(FiltersService.gaussianBlur);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/sharpen")
	public ResponseEntity<?> sharpen() throws IOException {
		filterAndSave(FiltersService.sharpenFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/laplacian")
	public ResponseEntity<?> laplacian() throws IOException {
		filterAndSave(FiltersService.laplacianFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/emboss")
	public ResponseEntity<?> emboss() throws IOException {
		filterAndSave(FiltersService.embossFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/sobelHorizontal")
	public ResponseEntity<?> sobelHorizontal() throws IOException {
		filterAndSave(FiltersService.sobelHorizontalFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/sobelVertical")
	public ResponseEntity<?> sobelVertical() throws IOException {
		filterAndSave(FiltersService.sobelVerticalFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/motionBlur")
	public ResponseEntity<?> motionBlur() throws IOException {
		filterAndSave(FiltersService.motionBlurFilter);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/findHorizontalLines")
	public ResponseEntity<?> findHorizontalLines() throws IOException {
		filterAndSave(FiltersService.findHorizontalEdges);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/findVerticalLines")
	public ResponseEntity<?> findVerticalLines() throws IOException {
		filterAndSave(FiltersService.findVerticalEdges);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/highPass")
	public ResponseEntity<?> highPass() throws IOException {
		filterAndSave(FiltersService.highPassFilter);
		return ResponseEntity.noContent().build();
	}

	private void filterAndSave(int[] filterType) {
		BufferedImage buf;
		try {
			buf = ImageIO.read(backendFile);
			buf = filtersService.filter(buf, filterType);
			ImageIO.write(buf, "bmp", backendFile);
			ImageIO.write(buf, "bmp", frontendFile);
		} catch (IOException e) {
			System.out.println("Error processing image " + e);
		}

	}

	@RequestMapping("/filter")
	public ResponseEntity<?> filter() throws IOException {
		BufferedImage buf = ImageIO.read(backendFile);
		buf = filtersService.filter(buf, FiltersService.gaussianBlur);
		ImageIO.write(buf, "bmp", backendFile);
		String path = "C:/Users/piotr/Desktop/grafika projekt/ui/grafika/src/assets/bmp/" + backendFilename;
		File frontendFile = new File(path);
		ImageIO.write(buf, "bmp", frontendFile);
		return ResponseEntity.noContent().build();
	}

}
