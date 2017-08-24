package venapis;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class checkalive {

	@RequestMapping("/alive")
	public @ResponseBody String checkservice(){
		return "ok";
	}
		
	@RequestMapping("/status")
	public String status() {
		return "\"Good\"";
	}
}
