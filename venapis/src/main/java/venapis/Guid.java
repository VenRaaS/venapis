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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class Guid {

	private static final Logger log = Logger.getLogger(Guid.class);
	private static final org.apache.log4j.Logger guidlog = Logger.getLogger("guidLog");
	private Gson gson = new Gson();

	@RequestMapping("/vengu")
	public @ResponseBody String greeting(
			@RequestParam(value = "id", defaultValue = "n") String id,
			@RequestParam(value = "pt", defaultValue = "n") String pt,
			@RequestHeader(value = "user-agent", defaultValue = "n") String agent,
			@RequestHeader(value = "X-FORWARDED-FOR", defaultValue = "n") String client_ip,
			@RequestHeader(value = "Origin", defaultValue = "n") String origin_host,
			@RequestHeader(value = "Host", defaultValue = "n") String api_host,
			@CookieValue(value = "venguid", defaultValue = "n") String venguid,
			HttpServletRequest request, HttpServletResponse response) {
		
		if (origin_host.equals("n")) {
			if (id.equals("n"))
				return "";
			origin_host = ((pt.equals("a"))? "https://":"http://")+id ;
		}		
		
		if (client_ip.equals("n"))
			client_ip = request.getRemoteAddr();
		if (venguid.equals("n")) {

			try {
				venguid = String.format(
						"%s-%s-%s",
						UUID.randomUUID().toString(),
						UUID.nameUUIDFromBytes(InetAddress.getLocalHost().getHostName().getBytes()).toString(), 
						UUID.nameUUIDFromBytes(client_ip.getBytes()).toString());

				Cookie guidCookie = new Cookie("venguid", venguid);
				guidCookie.setMaxAge(157680000); // 157680000 = 5year
				guidCookie.setDomain(api_host.replace(":8080", ""));
				guidCookie.setPath("/");
				response.addCookie(guidCookie);
				
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("agent", agent);
				parameters.put("client_ip", client_ip);
				parameters.put("origin", origin_host);
				parameters.put("ven_guid", venguid);
				parameters.put(
						"api_logtime",
						ZonedDateTime.now().format(
								DateTimeFormatter.ISO_ZONED_DATE_TIME));
				guidlog.info(gson.toJson(parameters));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//HTTP CORS protocol
		response.setHeader("Access-Control-Allow-Origin",origin_host);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		return venguid;

	}

}
