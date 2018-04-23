package de.DiscordBot.Commands.Profile.Achievements;

import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.Message;

public abstract class Achievement {

	public Achievement(boolean messages) {
		AchievementManager.achievements.put(this, messages);
	}
	
	public Achievement() {
		AchievementManager.achievements.put(this, true);
	}
	
	public abstract void process(Message m, Config c);
	
	
}
