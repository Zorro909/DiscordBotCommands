package de.DiscordBot.Commands.Profile;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class ProfileCommand extends DiscordCommand{

	static Config config;
	
	public ProfileCommand(String cmdName, String[] commandAliases, String description, String usage) {
		super(cmdName, commandAliases, description, usage);
		config = getGlobalConfig();
		DiscordBot.startService(new AchievementService(config));
		
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		// TODO Auto-generated method stub
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
