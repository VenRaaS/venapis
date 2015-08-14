package venapis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Logapi {

	private static final org.apache.log4j.Logger datalog = Logger.getLogger("DataLog");
	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String hostname = null;

	@RequestMapping("/log")
	public @ResponseBody String logapi(
			@RequestHeader(value = "user-agent", defaultValue = "n") String agent,
			@RequestHeader(value = "X-FORWARDED-FOR", defaultValue = "n") String client_ip,
			@RequestHeader(value = "Origin", defaultValue = "n") String origin_host,
			HttpServletRequest request, HttpServletResponse response) {

		if (client_ip.equals("n"))
			client_ip = request.getRemoteAddr();

		Map<String, String[]> parameters = new HashMap<String, String[]>(request.getParameterMap());
		
		parameters.put("agent",new String[] { agent});
		parameters.put("request_method", new String[] {request.getMethod()});
		parameters.put("api_logtime",new String[] { ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)});
		parameters.put("client_ip",new String[] { client_ip});
		
		try {
			if(hostname == null)
				hostname = InetAddress.getLocalHost().getHostName();
			parameters.put("api_loghost", new String[] {hostname});
		} catch (UnknownHostException e) {
			// ignore
		}
		datalog.info(gson.toJson(parameters));

		response.setHeader("Access-Control-Allow-Origin", origin_host);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");

		/**Important : Always return a empty string with this API. For the case of jsonp GET request, a empty string will be return. 
		 * If not empty string, it could be an error in front-end.
		 */
		return "";

	}
	
}
