package de.DiscordBot.Commands.Profile.Achievements.Types;

import java.io.IOException;

import de.DiscordBot.Commands.Profile.ProfileCommand;
import de.DiscordBot.Commands.Profile.Achievements.Achievement;
import de.DiscordBot.Commands.Profile.Achievements.AchievementManager;
import de.DiscordBot.Config.Config;
import javautils.UtilHelpers.FileUtils;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class CounterAchievement implements Achievement {

	String[] progressStages;
	String id;
	
	
	public CounterAchievement(String id, String[] progress) {
		this.id = id.trim();
		for(int i = 0;i<progress.length;i++) {
			progress[i] = progress[i].trim();
		}
		this.progressStages = progress;
	}
	
	public static CounterAchievement createStaticCounterAchievement(String id, String resourceFile) {
		return new CounterAchievement(id, readResources(resourceFile)) {
			
			@Override
			public boolean processCounter(Message m) {
				return false;
			}
			
			@Override
			public void process(Message m, Config c) {
			}
		};
	}

	public abstract boolean processCounter(Message m);

	public void increaseCount(User u, MessageChannel reward) {
		String id = this.id + "_" + u.getId();
		Config c = ProfileCommand.config;
		c.setIntValue(id, c.getIntValue(id, 0) + 1);
		int amount = c.getIntValue(id, 0);
		for (int i = 0; i < progressStages.length; i = i + 2) {
			if (Integer.valueOf(progressStages[i]) == amount) {
				AchievementManager.achievedAchievement(reward, u,
						(String) progressStages[i + 1]);
				break;
			}
		}
	}
	
	@Override
	public void process(Message m, Config c) {
		if(processCounter(m)){
			increaseCount(m.getAuthor(), m.getChannel());
		}
	}
	
	protected static String[] readResources(String fileName) {
		try {
			return FileUtils.readAll(Achievement.class.getResourceAsStream(fileName)).split(":|\\R");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new String[] {};
		}
	}

}
