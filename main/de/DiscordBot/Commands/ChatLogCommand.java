package de.DiscordBot.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import de.DiscordBot.CommandExecutor;
import de.DiscordBot.ChatLog.ChatLog;
import de.DiscordBot.ChatLog.ChatLogChannel;
import de.DiscordBot.ChatLog.ChatLogMessage;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.User;

public class ChatLogCommand extends DiscordCommand {

	public ChatLogCommand() {
		super("chatlog", new String[] { "clog" }, "You can view stats for the logged ChatLog of the Bot here",
				"\\chatlog [stats|quote|wordranking] {Mention}");
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

			List<Entry<String, Integer>> enc = getSortedCountedMessages(m.getGuild());

			Entry<String, Integer> msg = enc.iterator().next();
			eb.addField(new Field("Most sent Message (" + msg.getValue() + "):", msg.getKey() + "", false));
			return eb.build();
		} else if (args[0].equalsIgnoreCase("quote")) {
			User quoting = m.getAuthor();
			if (m.getMentionedUsers().size() > 0) {
				quoting = m.getMentionedUsers().get(0);
			}
			ArrayList<String> msg = new ArrayList<String>();
			ChatLog cl = CommandExecutor.getChatLog();
			cl.countMessages(m.getGuild());
			for (ChatLogChannel clc : cl.listChannels(m.getGuild()).values()) {
				for (ChatLogMessage clm : clc.clm) {
					if (clm.content.length() < 500 && clm.user.equalsIgnoreCase(quoting.getName())) {
						msg.add(clm.content);
					}
				}
			}
			if (msg.isEmpty()) {
				return new MessageBuilder().append("Sorry... but this user never wrote anything while I was here")
						.build();
			} else {
				return new EmbedBuilder().setAuthor(quoting.getName()).setThumbnail(quoting.getAvatarUrl())
						.addField("", msg.get(new Random().nextInt(msg.size())), false).build();
			}
		} else if (args[0].equalsIgnoreCase("wordranking")) {
			ChatLog cl = CommandExecutor.getChatLog();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Chatlog Wordranking");
			cl.countMessages(m.getGuild());

			List<Entry<String, Integer>> enc = getSortedCountedMessages(m.getGuild());
			Iterator<Entry<String, Integer>> it = enc.iterator();
			for (int i = 1; i <= 10; i++) {
				if (!it.hasNext()) {
					break;
				}
				Entry<String, Integer> msg = it.next();
				eb.addField(new Field(i + ". (" + msg.getValue() + "):",
						msg.getKey().length() > 256 ? msg.getKey().substring(0, 250) + "..." : msg.getKey(), true));
			}
			return eb.build();
		}
		return new MessageBuilder().append("Usage: " + getUsage()).build();
	}

	public List<Entry<String, Integer>> getSortedCountedMessages(Guild g) {
		LinkedHashMap<String, Integer> encounter = new LinkedHashMap<String, Integer>();
		for (ChatLogChannel clc : CommandExecutor.getChatLog().listChannels(g).values()) {
			for (ChatLogMessage clm : clc.clm) {
				if (encounter.containsKey(clm.content)) {
					if (clm.content.isEmpty()) {
						continue;
					}
					encounter.put(clm.content, encounter.get(clm.content) + 1);
				} else {
					encounter.put(clm.content, 1);
				}
			}
		}

		List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>();
		entries.addAll(encounter.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
				return -1 * a.getValue().compareTo(b.getValue());
			}
		});
		return entries;
	}

	@Override
	public void setupCommandConfig(Guild g, Config cfg) {
		// TODO Auto-generated method stub

	}

}
