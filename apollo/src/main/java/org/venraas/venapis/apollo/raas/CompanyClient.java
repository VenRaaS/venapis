package org.venraas.venapis.apollo.raas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venraas.venapis.apollo.mappings.Com_pkgs;
import org.venraas.venapis.common.Constant;
import org.venraas.venapis.common.Utility;
import org.venraas.venapis.context.Config;

import java.net.InetAddress;
import java.util.Base64;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CompanyClient {

    private static final String VENRAAS_INDEX_NAME = "venraas";
    
    static private final String TYPE_NAME = "com_pkgs"; 
    
    private static final Logger VEN_LOGGER = LoggerFactory.getLogger(CompanyClient.class);    
       
    
    public CompanyClient() { 
    	
    }

    public String getCodeName(String token) throws Exception {
        VEN_LOGGER.info("caching getCodeName({})", token);
        
        String codeName = "";
        
        if (null == token || token.isEmpty()) return codeName;        
        
        try {            
//            Config conf = Config.getInstance();
//            String hostname_es_ww = conf.getEs_host_westernwall();
//            if (null == hostname_es_ww) 
//                hostname_es_ww = InetAddress.getLocalHost().getHostName();
            
            String hostname_es_ww = "es-comp.venraas.private";

            //-- query $code_name based on given $token
            //   https://www.elastic.co/guide/en/elasticsearch/reference/1.7/search-uri-request.html#search-uri-request
            //   e.g. qUri = "http://${hostPath}/_search?q=${tokenKV}&sort=update_dt:desc";
            String username = "elastic";
            String password = System.getenv("ES_COMP_PWD");
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String authHeader = "Basic " + encodedAuth;
            
            String hostPath = String.format("http://%s:9200/%s_%s/_search?", hostname_es_ww, VENRAAS_INDEX_NAME, TYPE_NAME);
            String tokenKV = String.format("%s:%s", String.join(".", Com_pkgs.companies, Com_pkgs.token), token);            
            String query= String.format("q=%s&sort=%s:desc&size=1", tokenKV, Com_pkgs.update_dt);
            String qUri = hostPath + query;
            
            Content content = Request.Get(qUri)
            		.connectTimeout((int)Constant.TIMEOUT_SEARCH_MILLIS)
            		.setHeader("Authorization", authHeader)
            		.execute()
            		.returnContent();
            String jsonStr = content.asString();            
            JsonParser jp = new JsonParser();
            JsonObject obj = jp.parse(jsonStr).getAsJsonObject();
            JsonArray comps = obj.getAsJsonObject("hits")
                    .getAsJsonArray("hits")
                    .get(0)                    
                    .getAsJsonObject()
                    .getAsJsonObject("_source")
                    .getAsJsonArray(Com_pkgs.companies);

			for (JsonElement v : comps) {
				JsonObject c = (JsonObject) v;

				JsonElement tok_e = c.get(Com_pkgs.token);
				String tok = "";
				if (null != tok_e) {
					tok = tok_e.getAsString();
				}

				if (0 == tok.compareToIgnoreCase(token)) {

					JsonElement codeName_e = c.get(Com_pkgs.code_name);
					if (null != codeName_e) {
						codeName = codeName_e.getAsString();
						break;
					}
				}

			}

        } catch(Exception ex) {
            VEN_LOGGER.error(Utility.stackTrace2string(ex));
            VEN_LOGGER.error(ex.getMessage());
        }    
        
        return codeName;
    }


}
