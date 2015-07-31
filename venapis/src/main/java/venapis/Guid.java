package venapis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class Guid {

	//private static final Logger log = Logger.getLogger(Guid.class);
	private static final org.apache.log4j.Logger guidlog = Logger.getLogger("guidLog");
	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String hostname = null;

	@RequestMapping("/vengu")
	public @ResponseBody String greeting(
			@RequestParam(value = "id", defaultValue = "n") String id,
			@RequestParam(value = "pt", defaultValue = "n") String pt,
			@RequestParam(value = "typ", defaultValue = "n") String typ,
			@RequestHeader(value = "user-agent", defaultValue = "n") String agent,
			@RequestHeader(value = "X-FORWARDED-FOR", defaultValue = "n") String client_ip,
			@RequestHeader(value = "Origin", defaultValue = "n") String origin_host,
			@RequestHeader(value = "Host", defaultValue = "n") String api_host,
			@CookieValue(value = "venguid", defaultValue = "n") String venguid,
			HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		//If pt is "a", return as https connection.
		if (origin_host.equals("n")) {
			if (id.equals("n"))
				return "";
			origin_host = ((pt.equals("a"))? "https://":"http://")+id ;
		}		
		
		if (client_ip.equals("n"))
			client_ip = request.getRemoteAddr();
		
		try {
			if(hostname == null)
				hostname = InetAddress.getLocalHost().getHostName();
			
		} catch (UnknownHostException e) {
			guidlog.info(e.toString());
		}
		
		if (venguid.equals("n") && typ.equals("g")) {
				
			venguid = String.format(
					"%s.%s%s",
					UUID.randomUUID().toString(),
					hostname,
					ZonedDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE).replace("+0800", "")
					);

			Cookie guidCookie = new Cookie("venguid", venguid);
			guidCookie.setMaxAge(315360000); // 315360000 = 10year
			guidCookie.setDomain("venraas.tw");
			guidCookie.setPath("/");
			response.addCookie(guidCookie);
			parameters.put("ven_guid", venguid);
		}
		
		if(parameters.size() > 0) {
			parameters.put("agent", agent);
			parameters.put("client_ip", client_ip);
			parameters.put("origin", origin_host);
			parameters.put(
					"api_logtime",
					ZonedDateTime.now().format(
							DateTimeFormatter.ISO_ZONED_DATE_TIME));
			guidlog.info(gson.toJson(parameters));
		}

		//HTTP CORS protocol
		response.setHeader("Access-Control-Allow-Origin",origin_host);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		
		/* If type is "g", return guid string;
		 * If type is "s", return session string;
		 * Else return a space.
		*/
		String rtn=" ";
		switch(typ){	
		case "g":
			rtn=venguid;
			break;
		case "s":
			rtn= String.format(
					"%s.%s%s.se",
					UUID.randomUUID().toString(),
					hostname,
					ZonedDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE).replace("+0800", "")
					);
			break;
		default:
			break;
		}
		return rtn;

	}

}
