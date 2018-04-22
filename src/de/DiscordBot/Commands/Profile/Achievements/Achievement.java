package de.DiscordBot.Commands.Profile.Achievements;

import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.Message;

public interface Achievement {

	public void process(Message m, Config c);
	
}
