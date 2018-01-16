package com.memz.discord;

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
		Database.init();
		
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken("Mzk4MTkzNDAyNjM5ODEwNTYy.DS6-pQ.VePCNDXuQi2AinSsySed5LzK-Yk");
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
	
	
	
	
}
