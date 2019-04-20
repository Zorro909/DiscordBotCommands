package de.DiscordBot.Commands.Markov;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.DiscordBot;
import de.DiscordBot.ChatLog.ChatLog;
import de.DiscordBot.ChatLog.ChatLogChannel;
import de.DiscordBot.ChatLog.ChatLogMessage;
import de.DiscordBot.Commands.DiscordService;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MarkovService extends DiscordService {

	Markov global;
	ListenerAdapter listener;
	private static MarkovService service;
	private boolean ready = false;

	@Override
	public void run() {
		global = new Markov();
		ChatLog cl = CommandExecutor.getChatLog();
		for (Guild g : DiscordBot.discordJDABot().getGuilds()) {
			for (ChatLogChannel clc : cl.listChannels(g).values()) {
				clc.load();
				for (ChatLogMessage clm : clc.clm) {
					String content = clm.content;
					if (content.startsWith("!") || content.startsWith("t!") || content.startsWith("~")
							|| content.startsWith("\\") || content.startsWith("/") || content.startsWith("-")) {
						continue;
					}
					global.addWords(content);
				}
			}
		}

		DiscordBot.discordJDABot().addEventListener((listener = new ListenerAdapter() {
			@Override
			public void onMessageReceived(MessageReceivedEvent event) {
				if (event.getChannelType() == ChannelType.TEXT) {
					if (!event.getAuthor().isBot()) {
						Message m = event.getMessage();
						String content = m.getContent();
						if (!content.isEmpty()) {
							if (content.startsWith("!") || content.startsWith("t!") || content.startsWith("~")
									|| content.startsWith("\\") || content.startsWith("/") || content.startsWith("-")) {
								return;
							}
							global.addWords(content);
						}
					}
				}
			}
		}));
		ready = true;
	}

	public static String generateSentence(String seed) {
		if (service == null) {
			service = new MarkovService();
			DiscordBot.startService(service);
			try {
				service.waitTillReady();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return service.global.generateSentence(seed);
	}

	private void waitTillReady() throws InterruptedException {
		while (!ready) {
			Thread.sleep(500L);
		}
	}

	public static String generateSentence() {
		if (service == null) {
			service = new MarkovService();
			DiscordBot.startService(service);
			try {
				service.waitTillReady();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return service.global.generateSentence();
	}

	@Override
	public void shutdown() {
		service = null;
		DiscordBot.discordJDABot().removeEventListener(listener);
		global = null;
	}

}
