package grafika.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import grafika.model.CustomFilterOptions;
import grafika.model.FilterSelection;
import grafika.services.FiltersService;

@RestController
@RequestMapping("/photo")
@CrossOrigin(origins = "http://localhost:4200", methods = { RequestMethod.GET, RequestMethod.POST })
public class PhotoController {

	@Autowired
	private FiltersService filtersService;
	private File originalFile;
	private File backendFile;
	private File frontendFile;
	private String backendFilename;

	@RequestMapping("/upload")
	public void upload(@RequestParam MultipartFile file) throws IllegalStateException, IOException {
		backendFilename = file.getOriginalFilename();
		String userDir = System.getProperty("user.dir");
		userDir = userDir.replace('\\', '/');
		String backendPath = userDir + "/src/main/resources/static/bmp/" + backendFilename;
		String originalPath = userDir + "/src/main/resources/static/bmp/original.bmp";
		String frontendPath = "C:/Users/piotr/Desktop/grafika projekt/ui/grafika/src/assets/bmp/" + backendFilename;
		originalFile = new File(originalPath);
		frontendFile = new File(frontendPath);
		backendFile = new File(backendPath);
		file.transferTo(backendFile);
		file.transferTo(originalFile);
		file.transferTo(frontendFile);
	}

	@RequestMapping("/box")
	public ResponseEntity<?> box(@RequestBody FilterSelection filterSelection) throws IOException {
		System.out.println(filterSelection);
		filterAndSave(FiltersService.boxFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/gaussianBlur")
	public ResponseEntity<?> gaussianBlur(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.gaussianBlur, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/sharpen")
	public ResponseEntity<?> sharpen(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.sharpenFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/laplacian")
	public ResponseEntity<?> laplacian(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.laplacianFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/emboss")
	public ResponseEntity<?> emboss(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.embossFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/sobelHorizontal")
	public ResponseEntity<?> sobelHorizontal(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.sobelHorizontalFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/sobelVertical")
	public ResponseEntity<?> sobelVertical(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.sobelVerticalFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/motionBlur")
	public ResponseEntity<?> motionBlur(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.motionBlurFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/findHorizontalLines")
	public ResponseEntity<?> findHorizontalLines(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.findHorizontalEdges, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/findVerticalLines")
	public ResponseEntity<?> findVerticalLines(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.findVerticalEdges, filterSelection);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping("/highPass")
	public ResponseEntity<?> highPass(@RequestBody FilterSelection filterSelection) throws IOException {
		filterAndSave(FiltersService.highPassFilter, filterSelection);
		return ResponseEntity.noContent().build();
	}

	private void filterAndSave(int[] filterType, FilterSelection filterSelection) {
		BufferedImage buf;
		try {
			buf = ImageIO.read(backendFile);
			buf = filtersService.filter(buf, filterType, filterSelection);
			String frontendPath = frontendFile.getPath();
			frontendFile = new File(frontendPath);
			ImageIO.write(buf, "bmp", backendFile);
			ImageIO.write(buf, "bmp", frontendFile);
		} catch (IOException e) {
			System.out.println("Error processing image " + e);
		}
	}

	@RequestMapping(value = "/custom", method = RequestMethod.POST)
	public ResponseEntity<?> custom(@RequestBody CustomFilterOptions customFilterOptions) throws IOException {
		filterAndSave(customFilterOptions.getCustomFilter(), customFilterOptions.getFilterSelection());
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "/reset")
	public ResponseEntity<?> custom() throws IOException {
		BufferedImage buf;
		try {
			buf = ImageIO.read(originalFile);
			String frontendPath = frontendFile.getPath();
			frontendFile = new File(frontendPath);
			ImageIO.write(buf, "bmp", backendFile);
			ImageIO.write(buf, "bmp", frontendFile);
		} catch (IOException e) {
			System.out.println("Error processing image " + e);
		}
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(value = "/blend/{percent}")
	public ResponseEntity<?> blend(@PathVariable int percent) throws IOException {
		BufferedImage buf;
		try {
			buf = filtersService.blend(ImageIO.read(originalFile), ImageIO.read(backendFile), percent);
			String frontendPath = frontendFile.getPath();
			frontendFile = new File(frontendPath);
			ImageIO.write(buf, "bmp", backendFile);
			ImageIO.write(buf, "bmp", frontendFile);
		} catch (IOException e) {
			System.out.println("Error processing image " + e);
		}
		return ResponseEntity.noContent().build();
	}

}
