package org.venraas.venapis.context;


public class Config {	
	
	int conn_timeout;
	
	int conn_fail_cond_interval;
	
	int conn_fail_cond_count;
	
	int conn_fail_resume_interval;
	
	String es_host_westernwall;	

	static Config _conf = new Config();	
	

	private Config() { }

	static public Config getInstance() {		
		return _conf;
	}

	public int getConn_timeout() {
		return conn_timeout;
	}
	
	public void setConn_timeout(int ms) {
		this.conn_timeout = ms;
	}

	public int getConn_fail_cond_interval() {
		return conn_fail_cond_interval;
	}

	public void setConn_fail_cond_interval(int conn_fail_cond_interval) {
		this.conn_fail_cond_interval = conn_fail_cond_interval;
	}

	public int getConn_fail_cond_count() {
		return conn_fail_cond_count;
	}

	public void setConn_fail_cond_count(int conn_fail_cond_count) {
		this.conn_fail_cond_count = conn_fail_cond_count;
	}
	
	public int getConn_fail_resume_interval() {
		return conn_fail_resume_interval;
	}

	public void setConn_fail_resume_interval(int conn_fail_resume_interval) {
		this.conn_fail_resume_interval = conn_fail_resume_interval;
	}
	
	public String getEs_host_westernwall() {
		return es_host_westernwall;
	}

	public void setEs_host_westernwall(String es_host_westernwall) {
		this.es_host_westernwall = es_host_westernwall;
	}
	
}
