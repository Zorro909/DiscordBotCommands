package de.DiscordBot.Commands.Markov;

import java.lang.reflect.Array;

import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class MarkovCommand extends DiscordCommand {

	public MarkovCommand() {
		super("markov", new String[] {"speak"}, "Sends a custom generated sentence with a optional seed", "\\markov {seed}");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		String msg = "";
		if(args.length>0) {
			msg = MarkovService.generateSentence(String.join(" ", args));
		}else {
			msg = MarkovService.generateSentence();
		}
		return new EmbedBuilder().addField("Text", msg, true).setAuthor("Alia").build();
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
