package de.DiscordBot.Commands.Profile;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class ProfileCommand extends DiscordCommand {

	public static Config config;

	public ProfileCommand() {
		super("profile", new String[] {}, "WIP", "WIP");
		config = getGlobalConfig();
		DiscordBot.startService(new AchievementService(config));

	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		if (args.length > 0) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("picture")) {
					if (m.getAuthor().getAvatarUrl() == null) {
						return "You don't have a Avatar!";
					} else {
						return m.getAuthor().getAvatarUrl();
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setupCommandConfig(Guild g, Config cfg) {
		// TODO Auto-generated method stub

	}

}
