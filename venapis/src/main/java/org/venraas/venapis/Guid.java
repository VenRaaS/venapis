package org.venraas.venapis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.venraas.venapis.common.Utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class Guid {

	//private static final Logger log = Logger.getLogger(Guid.class);
	private static final Logger guidlog = LoggerFactory.getLogger("GuidLog");
	private static final Logger VEN_LOGGER = LoggerFactory.getLogger(VenapisController.class);
	
	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private String hostname = null;

	@RequestMapping("/vengu")
	public @ResponseBody String greeting(
			@RequestParam(value = "id", defaultValue = "n") String id, //domain name identity
			@RequestParam(value = "pt", defaultValue = "n") String pt, //request protocol type. pt="a" means "https://"
			@RequestParam(value = "typ", defaultValue = "n") String typ, //request uuid type. typ="g" return ven_guid; type="s" return ven_session 
			@RequestParam(value = "cbk", defaultValue = "n") String cbk, //"y" or "n". Using javascript call back function or not.
			@RequestHeader(value = "user-agent", defaultValue = "n") String agent,
			@RequestHeader(value = "X-FORWARDED-FOR", defaultValue = "n") String client_ip,
			@RequestHeader(value = "Origin", defaultValue = "n") String origin_host,
			@RequestHeader(value = "Host", defaultValue = "n") String api_host,
			@CookieValue(value = "venguid", defaultValue = "n") String venguid,
			HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		//If pt is "a", note as https connection.
		if (origin_host.equals("n")) {
			if (id.equals("n"))
				return "";
			origin_host = ((pt.equals("a"))? "https://":"http://")+id ;
		}		
		
		if (client_ip.equals("n"))
			client_ip = request.getRemoteAddr();
		
		try {
			// get this API server's host name
			if(hostname == null)
				hostname = InetAddress.getLocalHost().getHostName();
			
		} catch (UnknownHostException e) {
			VEN_LOGGER.warn(e.toString());
		}
		
		if (venguid.equals("n") && typ.equals("g")) {
			// The request does not has a guid in client side, gen. guid for it	
			venguid = String.format(
					"%s.%s%s",
					UUID.randomUUID().toString(),
					hostname,
					ZonedDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE).replace("+0800", "")
					);
			
			String secondLevelDomain = "venraas.tw";
			try {
				String reqUrl = request.getRequestURL().toString();
				URL url = new URL(reqUrl);
				String host = url.getHost();
				secondLevelDomain = host.substring(host.indexOf('.') + 1);
				VEN_LOGGER.info(secondLevelDomain);
			} catch (Exception ex){
				VEN_LOGGER.error(ex.getMessage());
				VEN_LOGGER.error(Utility.stackTrace2string(ex));
			}
			
			Cookie guidCookie = new Cookie("venguid", venguid);
			guidCookie.setMaxAge(315360000); // 315360000 = 10year
			guidCookie.setDomain(secondLevelDomain);
			guidCookie.setPath("/");
			response.addCookie(guidCookie);
			parameters.put("ven_guid", venguid);
		}
		
		if(parameters.size() > 0) {
			//Log every gen. guid information if it happen
			parameters.put("agent", agent);
			parameters.put("client_ip", client_ip);
			parameters.put("origin", origin_host);
			parameters.put(
					"api_logtime",
					ZonedDateTime.now().format(
							DateTimeFormatter.ISO_ZONED_DATE_TIME));
			guidlog.info(gson.toJson(parameters));
		}
		
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
		
		if(cbk.equals("y") && request.getMethod().equals("GET")){
			//HTTP(S) jsonp GET protocol, this do for some browser which can't support HTTP(S) CORS POST protocol
			rtn = "vengujsonpcallbk('"+typ+"','"+ rtn+"')";
		} else {
			//HTTP(S) CORS POST protocol
			response.setHeader("Access-Control-Allow-Origin",origin_host);
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		}
		
		return rtn;

	}

}
