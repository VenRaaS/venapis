package org.venraas.venapis.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utility {
	
	//-- ListenableFuture - https://github.com/google/guava/wiki/ListenableFutureExplained
	public static final ListeningExecutorService CacheRefreshLES;
	
	private static final Logger VEN_LOGGER = LoggerFactory.getLogger(Utility.class);

	
	static {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("cacheRefresherThread - %d").setDaemon(true).build();
//		ExecutorService es = Executors.newSingleThreadExecutor(threadFactory);
		ExecutorService es = Executors.newFixedThreadPool(5, threadFactory);
//		ExecutorService es = newFixedThreadPoolWithQueueSize(1, 1000, threadFactory);		
		CacheRefreshLES = MoreExecutors.listeningDecorator(es);
		VEN_LOGGER.info("cache refresher thread is creaded");
	}

	// -- http://stackoverflow.com/questions/2247734/executorservice-standard-way-to-avoid-to-task-queue-getting-too-full#answers-header
	// -- http://www.cnblogs.com/622698abc/archive/2013/03/16/2962382.html
	// -- http://dongxuan.iteye.com/blog/901689
	// -- http://givemepass.blogspot.tw/2015/10/threadpool.html
	private static ExecutorService newFixedThreadPoolWithQueueSize(int nThreads, int queueSize, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(
				nThreads, nThreads, 60L, TimeUnit.SECONDS, 
				new ArrayBlockingQueue<Runnable>(queueSize, true), 
				threadFactory,
				new ThreadPoolExecutor.DiscardPolicy());
	}

	static public String stackTrace2string(Exception ex) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		return sw.toString();
	}
	
	static public String now() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = new Date();
    	
		return dateFormat.format(date);		
	}
	
	static public long duration_sec (Date beg, Date end) {
		long secs = TimeUnit.MILLISECONDS.toSeconds(end.getTime() - beg.getTime());
		return secs;
	}

	static public <T> T json2instance(String jsonStr, Class<T> classOfT) throws JsonSyntaxException {
		Gson g = new Gson();
    	T obj = g.fromJson(jsonStr, classOfT);
        return obj;
	}
	
	static public String dtFormat(String in_dt) {
		String rt_dt = null;
		
		if (null == in_dt || in_dt.isEmpty())
			return rt_dt;
        
		ZonedDateTime zdt = null;
        try {
            long epoch;
            
            switch (in_dt.length()) {
                //-- epoch second, e.g. 1528732666
                case 10:
                    epoch = Long.valueOf(in_dt);
                    epoch *= 1000;
                    zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.of("UTC"));
                    break;
                //-- epoch milliseconds, e.g. 1528732666416
                case 13:
                    epoch = Long.valueOf(in_dt);
                    zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.of("UTC"));
                    break;
                //-- ISO_ZONED_DATE_TIME, ISO_INSTANT, e.g. 2018-06-11T11:57:46.416+08:00[Asia/Taipei], 2018-06-26T09:29:27Z
                default:
                    zdt = ZonedDateTime.parse(in_dt, DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.of("UTC")));
            }
            
            rt_dt = zdt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME.withZone(ZoneId.systemDefault()));
        }
        catch (Exception e) {
			VEN_LOGGER.error(e.getMessage());
			VEN_LOGGER.error(Utility.stackTrace2string(e));
        }        
        
        return  rt_dt;
	}


}
