package com.memz.discord;

/**
 * Holds the user's discord ID, burst address and numeric address
 * @author Robert
 *
 */
public class User {

	private String userId;
	private String burstAddress;
	private String burstNumeric;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBurstAddress() {
		return burstAddress;
	}
	public void setBurstAddress(String burstAddress) {
		this.burstAddress = burstAddress;
	}
	public String getBurstNumeric() {
		return burstNumeric;
	}
	public void setBurstNumeric(String burstNumeric) {
		this.burstNumeric = burstNumeric;
	}
	
}
