package de.DiscordBot.Commands.Profile.Achievements;

import java.util.LinkedList;

import de.DiscordBot.Commands.Profile.Achievements.Commands.CommandAchievement;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;


public class AchievementManager {

	static LinkedList<Achievement> achievements = new LinkedList<Achievement>();
	
	static {
		achievements.add(new CommandAchievement());
	}
	
	
	
	public static void processMessage(Message m, Config c) {
		for(Achievement a : achievements) {
			a.process(m, c);
		}
	}
	
	public static void achievedAchievement(TextChannel tc, User u, Config c, String achievement) {
		MessageBuilder mb = new MessageBuilder();
		mb.append("Hoooray ").append(u).append(", you unlocked the Achievement \"" + achievement + "\" :smile:");
	}
	
}
