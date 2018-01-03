package com.memz.discord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 * Main bot class
 * @author Robert
 *
 */
public class Bot {

	public static JDA jda;
	
	/**
	 * Main entry point.
	 * @param args
	 */
	public static void main(String args[]) {
		initDB();
		
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken("YOUR TOKEN HERE");
		builder.setAutoReconnect(true);
		builder.setStatus(OnlineStatus.ONLINE);
		
		builder.addEventListener(new ReadyListener());
		builder.addEventListener(new MessageListener());
		
		try {
			jda = builder.buildBlocking();
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RateLimitedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Check whether the database table exists, and if not, create it.
	 */
	public static void initDB() {
		boolean ok = false;
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			String testSQL = "select count(*) from TB_USERS;";
			try(PreparedStatement s = c.prepareStatement(testSQL)) {
				try(ResultSet rs = s.executeQuery()) {
					rs.next();
					System.out.println("There are "+rs.getInt(1)+" known users.");
					ok = true;
				}
			} catch (SQLException e) {
				//Don't panic, table doesn't exist
			}
		} catch (SQLException e) {
			//Panic! Cannot connect to DB
		}
		
		if(!ok) {
			System.out.println("Creating Database");
			
			try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
				String createSQL = "create table TB_USERS (USERID varchar(255),BURSTADDRESS varchar(255), BURSTNUMERIC varchar(255));";
				try(Statement s = c.createStatement()) {
					try {
						s.execute(createSQL);
						ok = true;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
