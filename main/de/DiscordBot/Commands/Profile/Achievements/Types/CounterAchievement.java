package de.DiscordBot.Commands.Profile.Achievements.Types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.DiscordBot.Commands.Profile.ProfileCommand;
import de.DiscordBot.Commands.Profile.Achievements.Achievement;
import de.DiscordBot.Commands.Profile.Achievements.AchievementManager;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class CounterAchievement extends Achievement {

	String[] progressStages;
	String id;

	public CounterAchievement(String id, String[] progress) {
		this.id = id.trim();
		for (int i = 0; i < progress.length; i++) {
			progress[i] = progress[i].trim();
		}
		this.progressStages = progress;
	}

	public CounterAchievement(String id, String[] progress, boolean messages) {
		super(messages);
		this.id = id.trim();
		for (int i = 0; i < progress.length; i++) {
			progress[i] = progress[i].trim();
		}
		this.progressStages = progress;
	}

	public static CounterAchievement createStaticCounterAchievement(String id, String resourceFile) {
		return new CounterAchievement(id, readResources(resourceFile), false) {
			@Override
			public boolean processCounter(Message m) {
				return false;
			}
		};
	}

	public abstract boolean processCounter(Message m);

	public void increaseCount(User u, MessageChannel reward) {
		String id = this.id + "_" + u.getId() + "_count";
		Config c = ProfileCommand.config;
		c.setIntValue(id, c.getIntValue(id, 0) + 1);
		int amount = c.getIntValue(id, 0);
		for (int i = 0; i < progressStages.length; i = i + 2) {
			if (Integer.valueOf(progressStages[i]) == amount) {
				AchievementManager.achievedAchievement(reward, u, (String) progressStages[i + 1]);
				break;
			}
		}
	}

	@Override
	public void process(Message m, Config c) {
		if (processCounter(m)) {
			increaseCount(m.getAuthor(), m.getChannel());
		}
	}

	protected static String[] readResources(String fileName) {
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(Achievement.class.getResourceAsStream(fileName)));
			String txt = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				txt += line + "\n";
			}
			return txt.split(":|\\R");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new String[] {};
		}
	}

}
