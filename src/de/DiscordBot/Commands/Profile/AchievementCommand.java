package de.DiscordBot.Commands.Profile;

import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class AchievementCommand extends DiscordCommand {

	public AchievementCommand() {
		super("achievements", new String[] {}, "Let's you look at your achievements (there are many hidden ones)", "\\achievements [page]");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		
		return null;
	}

	@Override
	public void setupCommandConfig(Guild g, Config cfg) {
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigPage createRemoteConfigurable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRemoteConfigurable() {
		// TODO Auto-generated method stub
		return false;
	}

}
