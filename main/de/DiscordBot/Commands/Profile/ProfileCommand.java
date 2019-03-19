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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setupCommandConfig(Guild g, Config cfg) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isRemoteConfigurable() {
        // TODO Auto-generated method stub
        return false;
    }

}
