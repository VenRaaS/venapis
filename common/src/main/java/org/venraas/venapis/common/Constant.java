package org.venraas.venapis.common;

public class Constant {
	
	public static final String HERMES_INDEX_SUFFIX = "_hermes";
		
	//-- 100%
	public static final double TRAFFIC_PERCENT_NORMAL = 1.0;	
	
	public static final int MAX_NUM_GROUPS = 100;
	
	public static final String NORMAL_GROUP_KEY = "normal";		
	
	
	public final static long CACHE_SIZE_10K = 10 * 1000;
	
	public final static long CACHE_SIZE_1M = 1 * 1000 * 1000;
	
	public final static long NUM_TIMEUNIT_0 = 0;
	
	public final static long NUM_TIMEUNIT_5 = 5;
	
	public final static long NUM_TIMEUNIT_10 = 10;
	
	public final static long NUM_TIMEUNIT_30 = 30;
	
	
	public final static int CONNECTION_POOL_MAX_TOTAL = 1024;
	public final static int CONNECTION_POOL_MAX_PER_ROUTE = 1000;
	public final static int CONNECTION_WAIT_TO_EVICATION_MS = 10000;
	
	//-- HTTP header
	public final static String COOOKIE = "Cookie";
	
	public final static int MAX_SIZE_FORWARD_HEADERS = 10;
	
	//-- Local cache key delimiter	
	public final static char FUNCTION_DELIMITER = '?';
	
	public final static char PARAM_DELIMITER = '&';

	//-- ES timeout
	public final static long TIMEOUT_SEARCH_MILLIS = 300;
	
	public final static long TIMEOUT_INDEX_MILLIS = 1000;
	
	//-- ES
	public final static int DEFAULT_QUERY_SIZE = 1000;
	
	
}
