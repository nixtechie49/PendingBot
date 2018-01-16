package com.memz.discord;

/**
 * Holds the user's discord ID, burst address and numeric address
 * @author Robert
 *
 */
public class User {

	private String userId;
	
	public User(String userId) {
		this.userId = userId;
		String result = Database.executeUnique("TB_USERS", "USERID", "USERID", userId);
		if(result == null) {
			Database.executeInsert("TB_USERS", "USERID", userId);
		}
	}
	
	public String getUserId() {
		return userId;
	}
	public String getBurstAddress() {
		String address = Database.executeUnique("TB_USERS", "BURSTADDRESS", "USERID", userId);
		return address;
	}
	public void setBurstAddress(String burstAddress) {
		Database.executeUpdate("TB_USERS", "BURSTADDRESS", burstAddress, "USERID", userId);
	}
	public String getBurstNumeric() {
		String address = Database.executeUnique("TB_USERS", "BURSTNUMERIC", "USERID", userId);
		if(address == null && getBurstAddress() != null) {
			address = HttpGet.getBurstNumeric(this);
			setBurstNumeric(address);
		}
		return address;
	}
	public void setBurstNumeric(String burstNumeric) {
		Database.executeUpdate("TB_USERS", "BURSTNUMERIC", burstNumeric, "USERID", userId);
	}
	public String getPoolName() {
		String poolName = Database.executeUnique("TB_USERS", "POOLNAME", "USERID", userId);
		return poolName;
	}
	public void setPoolName(String poolName) {
		Database.executeUpdate("TB_USERS", "POOLNAME", poolName, "USERID", userId);
	}
	public String getLastPending() {
		String lastPending = Database.executeUnique("TB_USERS", "LASTPENDING", "USERID", userId);
		return lastPending;
	}
	public void setLastPending(String lastPending) {
		Database.executeUpdate("TB_USERS", "LASTPENDING", lastPending, "USERID", userId);
	}
}
