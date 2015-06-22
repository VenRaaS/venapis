package venapis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class checkalive {

	@RequestMapping("/alive")
	public @ResponseBody String checkservice(){
		return "ok";
	}
}
