package de.DiscordBot.Commands.Profile;

import de.DiscordBot.Commands.DiscordService;
import de.DiscordBot.Commands.Profile.Achievements.AchievementManager;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AchievementService extends DiscordService {

	Config conf;
	
	public AchievementService(Config config) {
		this.conf = config;
	}
	
	ListenerAdapter la;
	
	@Override
	public void run() {
		getJDA().addEventListener((la=new ListenerAdapter() {
			@Override
			public void onMessageReceived(MessageReceivedEvent event) {
				if (event.getChannelType() == ChannelType.TEXT) {
					AchievementManager.processMessage(event.getMessage(), conf);
				}
			}
		}));
	}

	@Override
	public void shutdown() {
		getJDA().removeEventListener(la);
	}

}
