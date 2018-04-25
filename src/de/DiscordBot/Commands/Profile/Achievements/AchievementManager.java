package de.DiscordBot.Commands.Profile.Achievements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
		for (Achievement a : achievements.keySet()) {
			if (achievements.get(a)) {
				a.process(m, c);
			}
		}
	}

	public static List<String> listAchievedAchievements(User u) {
		Pattern p = Pattern.compile("^achievement_" + u.getId() + "_(.*)$");
		ArrayList<String> keys = ProfileCommand.config.getKeys(p.pattern());
		Map<String, Boolean> bools = ProfileCommand.config.getBooleanValues(keys.toArray(new String[keys.size()]));
		
		return bools.entrySet().stream()
				.filter((Map.Entry<String, Boolean> entry) -> entry.getValue())
				.map((Map.Entry<String, Boolean> entry) -> URLDecoder.decode(entry.getKey()))
				.collect(Collectors.toList());
	}

	public static void achievedAchievement(MessageChannel tc, User u, String achievement) {
		ProfileCommand.config.setValue("achievement_" + u.getId() + "_" + URLEncoder.encode(achievement), true);
		MessageBuilder mb = new MessageBuilder();
		Message m = mb.append("Hoooray ").append(u)
				.append(", you unlocked the Achievement \"" + achievement + "\" :smile:").build();
		tc.sendMessage(m).queue();
	}

}
