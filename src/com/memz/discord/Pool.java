package com.memz.discord;

/**
 * Holds the user's discord ID, burst address and numeric address
 * @author Robert
 *
 */
public class Pool {

	private String poolName;
	
	public Pool(String poolName) {
		this.poolName = poolName;
		String result = Database.executeUnique("TB_POOLS", "POOLNAME", "POOLNAME", poolName);
		if(result == null) {
			Database.executeInsert("TB_POOLS", "POOLNAME", poolName);
		}
	}
	
	public String getPoolName() {
		return poolName;
	}
	public String getJson() {
		String json = Database.executeUnique("TB_POOLS", "JSON", "POOLNAME", poolName);
		return json;
	}
	public void setJson(String json) {
		Database.executeUpdate("TB_POOLS", "JSON", json, "POOLNAME", poolName);
	}
}
