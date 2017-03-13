package grafika.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photo")
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoController {

	
	@RequestMapping("/test")
	public String test() {
		return "test";
	}
}
