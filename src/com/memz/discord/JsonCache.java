package com.memz.discord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonCache {

	private static JsonCache instance;
	
	private Map<String, List<String>> json = new HashMap<String, List<String>>();
	private Map<String, Long> times = new HashMap<String, Long>();
	
	private JsonCache() {}
	
	public static JsonCache getInstace() {
		if(instance == null) {
			instance = new JsonCache();
		}
		return instance;
	}
	
	public Long getTime(String poolName) {
		Long time = times.get(poolName);
		if(time == null) {
			time = 0L;
		}
		return time;
	}
	
	public void putTime(String poolName, Long time) {
		times.put(poolName, time);
	}
	
	public List<String> getJson(String poolName) {
		return json.get(poolName);
	}
	
	public void putJson(String poolName, List<String> newJson) {
		json.put(poolName, newJson);
	}
}
