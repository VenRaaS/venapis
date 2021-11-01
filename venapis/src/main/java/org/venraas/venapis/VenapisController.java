package org.venraas.venapis;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venraas.venapis.apollo.raas.CompanyManager;
import org.venraas.venapis.common.Utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@RestController
public class VenapisController {

	private static final Logger DATE_LOGGER = LoggerFactory.getLogger("DataLog");
	private static final Logger VEN_LOGGER = LoggerFactory.getLogger(VenapisController.class);

	@RequestMapping("/log")
	String log(
			@RequestHeader(value = "user-agent", defaultValue = "n") String agent,
			@RequestHeader(value = "X-FORWARDED-FOR", defaultValue = "n") String client_ip,
			@RequestHeader(value = "Origin", defaultValue = "n") String origin_host,
			HttpServletRequest request, HttpServletResponse response) {

		//-- content-type:application/x-www-form-urlencoded, must be specified in request header 
		Map<String, String[]> parameters = new HashMap<String, String[]>(request.getParameterMap());

		try {
			if (client_ip.equals("n"))
				client_ip = request.getRemoteAddr();				

			//-- derive the $code_name according to the given $token
			String code_name = null;
			String token = null;
			String api_logtime = null;			

			for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
				//-- e.g. {"pageload":["{\"action\":\"pageload\",\"uid\":null,...}"]}
				//   k = "pageload"
				//   v = ["{\"action\":\"pageload\",\"uid\":null,...}"]
				String k = entry.getKey();
				String[] v = entry.getValue();
				if (1 <= v.length) {
					String actPayload = v[0];
					JsonParser jp = new JsonParser();
					JsonElement je = jp.parse(actPayload);
					if (je.isJsonObject()) {
						JsonObject actObj = je.getAsJsonObject();						

			            JsonElement token_je = actObj.get("token");
			            if (null != token_je) {
			            	token = token_je.getAsString();
			            	CompanyManager comMgr = new CompanyManager();
			            	code_name = comMgr.getCodeName(token);
			            } 
		  
			    		JsonElement logdt_je = actObj.get("api_logtime");
			    		if (null != logdt_je) {
			    			api_logtime = logdt_je.getAsString();
			    			api_logtime = Utility.dtFormat(api_logtime);
			    		}

			    		break;
		    		}
				}
			}

			if (null == code_name || code_name.equals("")) {
				code_name = "n";
				VEN_LOGGER.warn("unable to find the [code_name] according to the [token]: %s", token);
			}

			parameters.put("code_name", new String[] {code_name});			
			parameters.put("agent",new String[] { agent});
			parameters.put("request_method", new String[] {request.getMethod()});
			parameters.put("api_logtime",new String[] { (null != api_logtime) ? api_logtime : ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME) });
			parameters.put("client_ip",new String[] { client_ip});	

			String hostname = System.getenv("HOSTNAME");
			parameters.put("api_loghost", new String[] {hostname});			
		} catch (JsonSyntaxException ex){
			if (null != parameters) {
				VEN_LOGGER.error(String.format("%s: %s", ex.getMessage(), new Gson().toJson(parameters)));
			}
			VEN_LOGGER.error(Utility.stackTrace2string(ex));				
		} catch (Exception ex) {
			VEN_LOGGER.error(ex.getMessage());
			VEN_LOGGER.error(Utility.stackTrace2string(ex));
		}

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		DATE_LOGGER.info(gson.toJson(parameters));

		response.setHeader("Access-Control-Allow-Origin", origin_host);
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");

		/**
		 * Important : Always return a empty string with this API. For the case of jsonp GET request, a empty string will be return. 
		 * If not empty string, it could be an error in front-end.
		 */
		return "";

	}

}
