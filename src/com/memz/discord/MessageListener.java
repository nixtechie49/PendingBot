package com.memz.discord;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        	
        	//Process it
        	String[] userMessage = message.split(" ");
        	if(userMessage.length > 0) {
        		processMessage(event, userMessage);
        	}
        }
    }	
	
	public void processMessage(MessageReceivedEvent event, String[] message) {
		if(message[0].equalsIgnoreCase("!mined")) {
			processMined(event, message);
		}
		if(message[0].equalsIgnoreCase("!pools")) {
			processPools(event, message);
		}
		if(message[0].equalsIgnoreCase("!pool")) {
			processPool(event, message);
		}
		if(message[0].equalsIgnoreCase("!register")) {
			processRegister(event, message);
		}
		if(message[0].equalsIgnoreCase("!remove")) {
			processRemove(event);
		}
		if(message[0].equalsIgnoreCase("!help")) {
			processHelp(event);
		}
	}
	
	public void processHelp(MessageReceivedEvent event) {
		String text = "";
		text+= "**!register** - sign up with me!\r\n";
		text+= "**!pool** - choose your pool\r\n";
		text+= "**!remove** - unregister\r\n";
		text+= "**!mined** - report pending burst";
		event.getTextChannel().sendMessage(text).queue();
	}
	
	public void processPools(MessageReceivedEvent event, String[] message) {
		if(message.length == 4 && message[1].equalsIgnoreCase("add")) {
			String userId = event.getMember().getUser().toString();
			if(userId.equals("U:memran(183609316627054592)")) {
				String poolName = message[2];
				String json = message[3];
				Pool pool = new Pool(poolName);
				pool.setJson(json);
				event.getTextChannel().sendMessage("Pool added.").queue();
			} else {
				event.getTextChannel().sendMessage("Forbidden.").queue();
			}
		} else if(message.length == 3 && message[1].equalsIgnoreCase("remove")) {
			String poolName = message[2];
			Database.executeDelete("TB_POOLS", "POOLNAME", poolName);
			event.getTextChannel().sendMessage("Pool removed.").queue();
		} else {
			List<String> pools = Database.executeQuery("TB_POOLS", "POOLNAME");
			String knownPools = "";
			Iterator<String> it = pools.iterator();
			while(it.hasNext()) {
				knownPools+=it.next();
				if(it.hasNext()) {knownPools+=", ";}
			}
			event.getTextChannel().sendMessage("Known pools: "+knownPools).queue();
		}
	}
	
	public void processPool(MessageReceivedEvent event, String[] message) {
		if(message.length == 2) {
			String userId = event.getMember().getUser().toString();
			User user = new User(userId);
			String poolName = message[1];
			String json = Database.executeUnique("TB_POOLS", "JSON", "POOLNAME", poolName);
			if(json != null) {
				user.setPoolName(poolName);
				poolChangeSuccess(event);
			} else {
				unknownPool(event);
			}			
		} else {
			event.getTextChannel().sendMessage("Use !pools for a list of known pools, and then !pool POOLNAME to set it up.").queue();
		}
	}
	
	public void processRegister(MessageReceivedEvent event, String[] message) {
		if(message.length == 2) {
			String userId = event.getMember().getUser().toString();
			User user = new User(userId);
			String burstAddress = message[1].toUpperCase();
			Pattern p = Pattern.compile("BURST(-[A-Z0-9]{4}){3}-[A-Z0-9]{5}");
			Matcher m = p.matcher(burstAddress);
			if(m.matches()) {
				user.setBurstAddress(burstAddress);	
				String burstNumeric = user.getBurstNumeric();
				if(burstNumeric != null) {
					registerSuccess(event);
				}
			} else {
				badBurstAddressFormat(event);
			}
		} else {
			event.getTextChannel().sendMessage("Usage: !register BURST-XXXX-XXXX-XXXX-XXXXX").queue();
		}
	}
	
	public void processRemove(MessageReceivedEvent event) {
		String userId = event.getMember().getUser().toString();
		Database.executeDelete("TB_USERS", "USERID", userId);
		event.getTextChannel().sendMessage(event.getMember().getAsMention()+", your address is now forgotten.").queue();
	}
	
	public void processMined(MessageReceivedEvent event, String[] message) {
		if(message.length == 1) {
			String userId = event.getMember().getUser().toString();
			User user = new User(userId);
			if(user.getBurstAddress() != null) {
				if(user.getBurstNumeric() != null) {
					if(user.getPoolName() != null) {
						String pending = HttpGet.getPendingBurst(user);
						if(pending != null) {
							String out = pending;
							String lastPending = user.getLastPending();
							float last;
							if(lastPending != null) {
								last = Float.valueOf(lastPending);
								float pend = Float.valueOf(pending);
								if(pend > last) {
									float change = pend-last;
									out += " (+"+change+" since last check)";
								} else if(pend < last){
									out += " (+"+pend+" since payout)";
								} else {
									out += " (no change)";
								}
							}							
							user.setLastPending(pending);
							outputPending(event, out, user.getPoolName());
						} else {
							outputPendingUnknown(event);
						}
					} else {
						unknownPoolError(event);
					}
					return;
				}
			}
			unknownUserError(event);
		}
	}
	
	public void registerSuccess(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage(event.getMember().getAsMention()+", next configure your pool. Use !pools for a list of known pools, and then !pool POOLNAME to set it up.").queue();
	}
	public void poolChangeSuccess(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage(event.getMember().getAsMention()+", you can now use !mined to see your pending burst.").queue();
	}
	public void unknownPool(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage("Unknown pool.").queue();
	}
	public void badBurstAddressFormat(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage("Burst address must be of the form BURST-XXXX-XXXX-XXXX-XXXXX").queue();
	}
	public void unknownPoolError(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage("I don't know which pool you are in, "+event.getMember().getAsMention()+". Use !pools for a list of known pools, and then !pool POOLNAME to set it up.").queue();
	}
	public void unknownUserError(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage("I don't know your address, "+event.getMember().getAsMention()+". Use !register BURSTADDRESS instead.").queue();
	}
	public void outputPending(MessageReceivedEvent event, String pending, String poolName) {
		event.getTextChannel().sendMessage(event.getMember().getAsMention()+", you have "+pending+" burst pending from the "+poolName+" pool.").queue();
	}
	public void outputPendingUnknown(MessageReceivedEvent event) {
		event.getTextChannel().sendMessage(event.getMember().getAsMention()+", unable to find pending info!").queue();
	}
	
}
