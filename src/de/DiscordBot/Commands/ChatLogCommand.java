package de.DiscordBot.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
		super("chatlog", new String[] {"clog"}, "You can view stats for the logged ChatLog of the Bot here", "\\chatlog [stats|quote] {Mention}");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		if(args.length==0||args[0]==null) {
			return new MessageBuilder().append("Usage: " + getUsage());
		}
		if(args[0].equalsIgnoreCase("stats")) {
			ChatLog cl =  CommandExecutor.getChatLog();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Chatlog Stats");
			eb.addField(new Field("Total Messages captured", cl.countMessages(m.getGuild()) + "", true));
			HashMap<String, Integer> encounter = new HashMap<String, Integer>();
			long most = 0;
			String me = "";
			for (ChatLogChannel clc : cl.listChannels(m.getGuild()).values()) {
				for(ChatLogMessage clm : clc.clm) {
					if(encounter.containsKey(clm.content)) {
						if(clm.content.isEmpty())continue;
						encounter.put(clm.content, encounter.get(clm.content)+1);
						if(most<encounter.get(clm.content)) {
							most = encounter.get(clm.content);
							me = clm.content;
						}
					}else {
						encounter.put(clm.content, 1);
					}
				}
			}
			eb.addField(new Field("Most sent Message (" + most + "):", me + "", false));		
			return eb.build();
		}else if(args[0].equalsIgnoreCase("quote")) {
			if(m.getMentionedUsers().isEmpty()) {
				return new MessageBuilder().append("Usage: \\chatlog quote @Mention");
			}
			ArrayList<String> msg = new ArrayList<String>();
			ChatLog cl =  CommandExecutor.getChatLog();
			cl.countMessages(m.getGuild());
			for (ChatLogChannel clc : cl.listChannels(m.getGuild()).values()) {
				for(ChatLogMessage clm : clc.clm) {
					if(clm.content.length()<500&&clm.user.equalsIgnoreCase(m.getAuthor().getName())) {
						msg.add(clm.content);
					}
				}
			}
			if(msg.isEmpty()) {
				return new MessageBuilder().append("Sorry... but this user never wrote anything while I was here").build();
			} else {
				return new EmbedBuilder().setAuthor(m.getMentionedUsers().get(0).getName()).setThumbnail(m.getMentionedUsers().get(0).getAvatarUrl()).addField("", msg.get(new Random().nextInt(msg.size())),false).build();
			}
		}
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
