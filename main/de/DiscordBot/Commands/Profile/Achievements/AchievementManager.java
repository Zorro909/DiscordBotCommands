package de.DiscordBot.Commands.Profile.Achievements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.LinkedMap;

import com.google.common.collect.Lists;

import de.DiscordBot.Commands.Profile.ProfileCommand;
import de.DiscordBot.Commands.Profile.Achievements.Commands.CommandAchievement;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class AchievementManager {

	static LinkedMap<Achievement, Boolean> achievements = new LinkedMap<Achievement, Boolean>();

	static {
		new CommandAchievement();
		new MehChievement();
	}

	public static void processMessage(Message m, Config c) {
		for (Achievement a : achievements.keySet()) {
			if (achievements.get(a)) {
				a.process(m, c);
			}
		}
	}

	public static List<String> listAchievedAchievements(User u) {
		String ach = ProfileCommand.config.getValue("achievements_" + u.getId(), "");
		if(ach==null||ach.isEmpty()) {
			return new ArrayList<String>();
		}
		return Lists.newArrayList(ach.split(","));
	}

	public static void achievedAchievement(MessageChannel tc, User u, String achievement) {
		List<String> achievements = listAchievedAchievements(u);
		achievements.add(achievement);
		ProfileCommand.config.setValue("achievements_" + u.getId(),
				achievements.stream().collect(Collectors.joining(", ")));
		MessageBuilder mb = new MessageBuilder();
		Message m = mb.append("Hoooray ").append(u)
				.append(", you unlocked the Achievement \"" + achievement + "\" :smile:").build();
		tc.sendMessage(m).queue();
	}

}
