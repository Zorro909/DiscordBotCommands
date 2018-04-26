package de.DiscordBot.Commands.SMMRY;

import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Commands.SMMRY.SmmryAPI.Summary;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class SummaryCommand extends DiscordCommand{

	SmmryAPI smmry = new SmmryAPI("735262580D");
	
	public SummaryCommand() {
		super("summary", new String[] {"tldr"}, "Summarizes a webpage", "\\tldr [url]");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		if(args.length==0) {
			return new MessageBuilder().append("Usage: ").appendCodeBlock(getUsage(), null).build();
		}
		String url = String.join(" ", args);
		Summary s = smmry.newSummaryBuilder().website(url).withBreak().sentences(5).build();
		if(s.hasError()) {
			return new MessageBuilder().append("An Error occured...\nError Message: " + s.getMessage() + "\nError Code: " + s.getError()).build();
		}
		return new MessageBuilder().append("Summary for: " + url).appendCodeBlock(s.getSummary().replace("[BREAK]", "\n"), null).build();
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
