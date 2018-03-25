package de.DiscordBot.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.ChatLog.ChatLog;
import de.DiscordBot.ChatLog.ChatLogChannel;
import de.DiscordBot.ChatLog.ChatLogMessage;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class ChatLogCommand extends DiscordCommand {

	public ChatLogCommand() {
		super("chatlog", new String[] { "clog" }, "You can view stats for the logged ChatLog of the Bot here",
				"\\chatlog [stats|quote] {Mention}");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		if (args.length == 0 || args[0] == null) {
			return new MessageBuilder().append("Usage: " + getUsage()).build();
		}
		if (args[0].equalsIgnoreCase("stats")) {
			ChatLog cl = CommandExecutor.getChatLog();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Chatlog Stats");
			eb.addField(new Field("Total Messages captured", cl.countMessages(m.getGuild()) + "", true));

			LinkedHashMap<String, Integer> enc = getSortedCountedMessages(m.getGuild());

			String msg = enc.keySet().iterator().next();
			eb.addField(new Field("Most sent Message (" + enc.get(msg) + "):", msg + "", false));
			return eb.build();
		} else if (args[0].equalsIgnoreCase("quote")) {
			if (m.getMentionedUsers().isEmpty()) {
				return new MessageBuilder().append("Usage: \\chatlog quote @Mention").build();
			}
			ArrayList<String> msg = new ArrayList<String>();
			ChatLog cl = CommandExecutor.getChatLog();
			cl.countMessages(m.getGuild());
			for (ChatLogChannel clc : cl.listChannels(m.getGuild()).values()) {
				for (ChatLogMessage clm : clc.clm) {
					if (clm.content.length() < 500
							&& clm.user.equalsIgnoreCase(m.getMentionedUsers().get(0).getName())) {
						msg.add(clm.content);
					}
				}
			}
			if (msg.isEmpty()) {
				return new MessageBuilder().append("Sorry... but this user never wrote anything while I was here")
						.build();
			} else {
				return new EmbedBuilder().setAuthor(m.getMentionedUsers().get(0).getName())
						.setThumbnail(m.getMentionedUsers().get(0).getAvatarUrl())
						.addField("", msg.get(new Random().nextInt(msg.size())), false).build();
			}
		} else if (args[0].equalsIgnoreCase("wordranking")) {
			ChatLog cl = CommandExecutor.getChatLog();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Chatlog Wordranking");
			cl.countMessages(m.getGuild());

			LinkedHashMap<String, Integer> enc = getSortedCountedMessages(m.getGuild());

			String msg = enc.keySet().iterator().next();
			for (int i = 1; i <= 10; i++) {
				eb.addField(new Field(i + ". (" + enc.get(msg) + "):", (msg.length() > 256 ? msg.substring(0,250) + "..." : "") , true));
			}
			return eb.build();
		}
		return new MessageBuilder().append("Usage: " + getUsage()).build();
	}

	public LinkedHashMap<String, Integer> getSortedCountedMessages(Guild g) {
		LinkedHashMap<String, Integer> encounter = new LinkedHashMap<String, Integer>();
		for (ChatLogChannel clc : CommandExecutor.getChatLog().listChannels(g).values()) {
			for (ChatLogMessage clm : clc.clm) {
				if (encounter.containsKey(clm.content)) {
					if (clm.content.isEmpty())
						continue;
					encounter.put(clm.content, encounter.get(clm.content) + 1);
				} else {
					encounter.put(clm.content, 1);
				}
			}
		}

		List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>();
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
				return a.getValue().compareTo(b.getValue());
			}
		});
		return encounter;
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
