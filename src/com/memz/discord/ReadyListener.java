package com.memz.discord;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class ReadyListener implements EventListener {
	
	/**
	 * Output some basic debug info.
	 */
	public void onEvent(Event e) {
		if(e instanceof ReadyEvent) {
			String out = "\nThis bot is running on servers:\n";
			for(Guild g : e.getJDA().getGuilds()) {
				out += g.getName() + "(" + g.getId() + ")\n";
			}
			System.out.println(out);
		}
	}

}
