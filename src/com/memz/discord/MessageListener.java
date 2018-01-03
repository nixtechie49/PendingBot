package com.memz.discord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

	/**
	 * Listens for messages. 
	 * Anything starting with !mined is responded to
	 */
	@Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
        	//Ignore private messages
        } else {
        	//Get the message and remove any double spaces
        	String message = event.getMessage().getContentDisplay();
        	while(message.contains("  ")) {
        		message = message.replace("  ", " ");
        	}
        	
        	//Check for the !mined command
            if(message.startsWith("!mined")) {
            	String userId = event.getMember().getUser().toString();
            	
            	if(message.split(" ").length == 1) {
                	//No further argument. This needs the user to be known already
            		User user = getUser(userId);
            		if(user.getUserId() == null || user.getBurstAddress() == null || user.getBurstNumeric() == null) {
            			//User is not known, or addresses are not known
            			event.getTextChannel().sendMessage("I don't know your address, "+event.getMember().getAsMention()+" :frowning:. Use !mined BURST-ADDRESS instead").queue();
            		} else {
            			//User is known
            			String pending = HttpGet.getPendingBurst(user);
            			event.getTextChannel().sendMessage(event.getMember().getAsMention()+", you have "+pending+" burst pending. :smiley:").queue();
            		}
            	} else if(message.split(" ").length == 2) {
                	//One argument - either "remove" or the burst address
            		if(message.split(" ")[1].equalsIgnoreCase("remove")) {
            			//Delete user from DB
            			deleteUser(userId);
            			event.getTextChannel().sendMessage(event.getMember().getAsMention()+", your address is now forgotten. :sob:").queue();
            			return;
            		}
            		
            		//Get the user and parse the numeric address
            		User user = getUser(userId);
            		user.setUserId(userId);
            		String burstAddress = message.split(" ")[1];
            		user.setBurstAddress(burstAddress);
            		String burstNumeric = HttpGet.getBurstNumeric(user);
            		if(burstNumeric.length() == 0) {
            			//Unable to parse the numeric address
            			event.getTextChannel().sendMessage(event.getMember().getAsMention()+", could not find your address. :thinking:").queue();
            			return;
            		}
            		user.setBurstNumeric(burstNumeric);
            		storeUser(user);//Save for next time, so user can type !mined

            		//Get the pending amount from the json page
        			String pending = HttpGet.getPendingBurst(user);
        			event.getTextChannel().sendMessage(event.getMember().getAsMention()+", you have "+pending+" burst pending. :smiley:").queue();
            	} else {
            		//Generic failure to parse message
            		//event.getTextChannel().sendMessage("I don't understand! :thinking:").queue();
            	}
            }
        }
    }
	
	/**
	 * Retrieve the user from the database
	 * @param userId
	 * @return
	 */
	public User getUser(String userId) {
		User user = null;
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			String testSQL = "select * from TB_USERS where userId = '"+userId+"';";
			try(PreparedStatement s = c.prepareStatement(testSQL)) {
				try(ResultSet rs = s.executeQuery()) {
					rs.next();					
					user = new User();
					user.setUserId(rs.getString("USERID"));
					user.setBurstAddress(rs.getString("BURSTADDRESS"));
					user.setBurstNumeric(rs.getString("BURSTNUMERIC"));	
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * Delete user from the database
	 * @param userId
	 */
	public void deleteUser(String userId) {
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			String delSql = "delete from TB_USERS where userId = '"+userId+"';";
			try(Statement s = c.createStatement()) {
				try {
					s.execute(delSql);
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
	
	/**
	 * Persist the user to the database
	 * @param user
	 */
	public void storeUser(User user) {
		User test = getUser(user.getUserId());
		String sql = null;
		if(test.getUserId() != null) {
			sql = "update TB_USERS set BURSTADDRESS = '"+user.getBurstAddress()+"', BURSTNUMERIC = '"+user.getBurstNumeric()+"' where USERID = '"+user.getUserId()+"';";
		} else {
			sql = "insert into TB_USERS (USERID, BURSTADDRESS, BURSTNUMERIC) values ('"+user.getUserId()+"','"+user.getBurstAddress()+"','"+user.getBurstNumeric()+"');";
		}
		try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:burst_db", "SA", "")) {
			try(Statement s = c.createStatement()) {
				try {
					s.execute(sql);
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
