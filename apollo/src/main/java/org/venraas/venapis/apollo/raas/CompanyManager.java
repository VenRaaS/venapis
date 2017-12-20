package org.venraas.venapis.apollo.raas;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.venraas.venapis.common.Constant;
import org.venraas.venapis.common.Utility;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;


public class CompanyManager {
	
	static LoadingCache<String, String> _cache_company;
	
	private static final Logger VEN_LOGGER = LoggerFactory.getLogger(CompanyManager.class);
	
	
	static {
		//-- Guava cache - https://github.com/google/guava/wiki/CachesExplained#refresh
		_cache_company = CacheBuilder.newBuilder()
				.maximumSize(Constant.CACHE_SIZE_10K)
				.refreshAfterWrite(Constant.NUM_TIMEUNIT_10, TimeUnit.MINUTES)
				.build(
					new CacheLoader<String, String>() {
						public String load(String key) throws Exception {
							CompanyClient client = new CompanyClient();
							return client.getCodeName(key);
						}
						
						public ListenableFuture<String> reload (final String key, String oldVal) {
							//-- async call to get the value from source
							ListenableFuture<String> task = 
								Utility.CacheRefreshLES.submit(
									new Callable<String>() {
										public String call() throws Exception {
											return load(key);
										}
									});
							
			                return task;
						}
					}
				);			
	}
	
	public CompanyManager() {}

	public String getCodeName(String token) {	
		String codeName = "";
		
		try {
			codeName = _cache_company.get(token);
		} catch (Exception ex) {
			VEN_LOGGER.error(ex.getMessage());
			VEN_LOGGER.error(Utility.stackTrace2string(ex));
		}
		
		return codeName;
	}

}
