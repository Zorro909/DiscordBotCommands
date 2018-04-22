package de.DiscordBot.Commands.Profile.Achievements.Commands;

import de.DiscordBot.Commands.Profile.Achievements.Achievement;
import de.DiscordBot.Commands.Profile.Achievements.AchievementManager;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.Message;

public class CommandAchievement implements Achievement {

	Object[] progressStages = new Object[] {1, "1 Command To Go", 5, "5 Commands are Nothing", 25, "25 Commands - Nice Start", 50, "50 Commands - Getting Ready", 100, "100 Commands to Infinity", 250, "250 Commands - WOAH", 500, "500 Commands - Are you crazy?", 1000, "1000 CMDs... - NANI???", 5000, "5000 CMDs - How long is this bot running?", 9001, "9001 CMDs - OVER 9000", 10000, "10K CMDs - Last Resort"};
	
	@Override
	public void process(Message m, Config c) {
		if(m.getContent().startsWith("\\")) {
			String id = "commands_" + m.getAuthor().getId();
			c.setIntValue(id, c.getIntValue(id,0)+1);
			int amount = c.getIntValue(id, 0);
			for(int i = 0;i<progressStages.length;i=i+2) {
				if((int)progressStages[i]==amount) {
					AchievementManager.achievedAchievement(m.getTextChannel(), m.getAuthor(), c, (String)progressStages[i+1]);
					break;
				}
			}
		}
	}

}
