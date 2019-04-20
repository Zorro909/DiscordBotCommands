package de.DiscordBot.Commands.Markov;

import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class MarkovCommand extends DiscordCommand {

	public MarkovCommand() {
		super("markov", new String[] { "speak" }, "Sends a custom generated sentence with a optional seed",
				"\\markov {seed}");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		String msg = "";
		try {
			if (args.length > 0) {
				msg = MarkovService.generateSentence(String.join(" ", args));
			} else {
				msg = MarkovService.generateSentence();
			}
		} catch (Exception e) {
			return "I'm sorry, but I still did not gather enough Data with your specified starting word!";
		}
		if (msg.length() > 1800) {
			msg = msg.substring(0, 1800) + "...";
		}
		return new EmbedBuilder().addField("Text", msg, true).setAuthor("Alia").build();
	}

	@Override
	public void setupCommandConfig(Guild g, Config cfg) {
		// TODO Auto-generated method stub

	}

}
