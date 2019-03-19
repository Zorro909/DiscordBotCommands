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
        if (args.length > 0) {
            msg = MarkovService.generateSentence(String.join(" ", args));
        } else {
            msg = MarkovService.generateSentence();
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

    @Override
    public boolean isRemoteConfigurable() {
        // TODO Auto-generated method stub
        return false;
    }

}
