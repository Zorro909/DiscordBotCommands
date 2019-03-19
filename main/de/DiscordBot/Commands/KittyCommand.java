package de.DiscordBot.Commands;

import java.net.URLEncoder;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class KittyCommand extends DiscordCommand {

    public KittyCommand() {
        super("kitty", new String[] { "robo" }, "Generates a Image of a Kitty (or a robot) from a given input text",
                "\\kitty {text}");
        // TODO Auto-generated constructor stub
    }

    @Override
    public Object execute(String command, String[] args, Message m) {
        StringBuilder t = new StringBuilder();
        String text = null;
        if (args.length == 0) {
            char[] charArray = IntStream.rangeClosed('A', 'Z').mapToObj(c -> "" + (char) c)
                    .collect(Collectors.joining()).toCharArray();
            new Random().ints(8).forEach((i) -> t.append(charArray[i]));
            text = t.toString();
        } else {
            text = String.join(" ", args);
        }
        MessageBuilder mb = new MessageBuilder();
        mb.append("Here is your ");
        int set = 4;
        if (command.equalsIgnoreCase("kitty")) {
            mb.append("Kitty");
        } else if (command.equalsIgnoreCase("robo")) {
            mb.append("Robot");
            set = new Random().nextInt(3) + 1;
        }
        mb.append("\nhttp://robohash.org/" + URLEncoder.encode(text) + ".png?set=set" + set);
        return mb.build();
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
