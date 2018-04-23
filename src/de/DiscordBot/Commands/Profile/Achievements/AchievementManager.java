package de.DiscordBot.Commands.Profile.Achievements;

import java.net.URLEncoder;
import java.util.LinkedList;

import org.apache.commons.collections4.map.LinkedMap;

import de.DiscordBot.Commands.Profile.ProfileCommand;
import de.DiscordBot.Commands.Profile.Achievements.Commands.CommandAchievement;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;


public class AchievementManager {

	static LinkedMap<Achievement, Boolean> achievements = new LinkedMap<Achievement, Boolean>();
	
	static {
		new CommandAchievement();
	}
	
	
	
	public static void processMessage(Message m, Config c) {
		for(Achievement a : achievements.keySet()) {
			if(achievements.get(a)) {
			a.process(m, c);
			}
		}
	}
	
	public static void achievedAchievement(MessageChannel tc, User u, String achievement) {
		ProfileCommand.config.setValue("achievement_" + u.getId() + "_" + URLEncoder.encode(achievement), true);
		MessageBuilder mb = new MessageBuilder();
		Message m = mb.append("Hoooray ").append(u).append(", you unlocked the Achievement \"" + achievement + "\" :smile:").build();
		tc.sendMessage(m).queue();
	}
	
}
