package com.memz.discord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpGet {

	private static final String USER_AGENT = "Mozilla/5.0";

	/**
	 * Fetches and parses the json page to retieve the burst amount
	 * @param user
	 * @return
	 */
	public static String getPendingBurst(User user) {
		if(user.getBurstAddress() != null) {
			String poolName = user.getPoolName();
			String url = Database.executeUnique("TB_POOLS", "JSON", "POOLNAME", poolName);
			if(url == null) {
				return null;
			}
			
			Long time = JsonCache.getInstace().getTime(poolName);
			Long now = System.currentTimeMillis();
			
			List<String> lines = new ArrayList<String>();
			if(time + (1000*60) < now || JsonCache.getInstace().getJson(poolName) == null) {
				URL obj;
				try {
					obj = new URL(url);		
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", USER_AGENT);
			
					int responseCode = con.getResponseCode();
					System.out.println("\nSending 'GET' request to URL : " + url);
					System.out.println("Response Code : " + responseCode);
			
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						lines.add(inputLine);
					}
					in.close();	
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				JsonCache.getInstace().putTime(poolName, System.currentTimeMillis());
				JsonCache.getInstace().putJson(poolName, lines);
			} else {
				System.out.println("Loading Json for "+poolName+" from cache.");
				lines = JsonCache.getInstace().getJson(poolName);
			}
			
			for(String line : lines) {
				line = line.replaceAll(" ", "");
				line = line.replaceAll(",", "");
				if(line.startsWith("\""+user.getBurstNumeric()+"\":")) {
					return line.split(":")[1];
				}
			}
			
		}
		return null;
	}
	
	/**
	 * Parses the page to get the numeric account number for the user
	 * @param user
	 * @return
	 */
	public static String getBurstNumeric(User user) {
		if(user.getBurstAddress() != null) {
			String url = "http://explore.burstcoin.ro/account/"+user.getBurstAddress();
			URL obj;
			try {
				obj = new URL(url);		
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);
		
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
		
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				String page = response.toString();
				
				int s = page.indexOf("<title>");
				int e = page.indexOf("</title>");
				String numeric = page.substring(s, e);
				numeric = numeric.substring(numeric.lastIndexOf(" ")).trim();
				return numeric;
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}
}