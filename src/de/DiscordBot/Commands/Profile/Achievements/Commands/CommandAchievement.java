package de.DiscordBot.Commands.Profile.Achievements.Commands;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.Commands.Profile.Achievements.Types.CounterAchievement;
import net.dv8tion.jda.core.entities.Message;

public class CommandAchievement extends CounterAchievement {

	public CommandAchievement() {
		super("commands", readResources("CommandAchievement"));
	}

	@Override
	public boolean processCounter(Message m) {
		if(CommandExecutor.isCommand(m.getContent())) {
			return true;
		}
		return false;
	}

}
