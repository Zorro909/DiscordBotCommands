package de.DiscordBot.Commands.Profile;

import java.util.List;

import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Commands.Profile.Achievements.AchievementManager;
import de.DiscordBot.Config.Config;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class AchievementCommand extends DiscordCommand {

    public AchievementCommand() {
        super("achievements", new String[] {}, "Lets you look at your achievements (there are many hidden ones)",
                "\\achievements [page]");
        // TODO Auto-generated constructor stub
    }

    @Override
    public Object execute(String command, String[] args, Message m) {
        User u = m.getAuthor();
        List<String> achievements = AchievementManager.listAchievedAchievements(u);
        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.valueOf(args[0]);
            } catch (Exception e) {
                return new MessageBuilder().append("You need to specify a valid Page Number!").build();
            }
        }
        int start = (page - 1) * 8;
        if (achievements.size() < start + 1) {
            return new MessageBuilder().append("You don't have that many achievements...").build();
        }
        MessageBuilder mb = new MessageBuilder();
        mb.append("Achievement List:\nPage " + page);
        String display = String.join("\n",
                achievements.subList(start, achievements.size() - 1 < start + 8 ? achievements.size() : start + 8));
        mb.appendCodeBlock(display, null);
        mb.append("Pages: ");
        for (int i = 1; i - 1 < achievements.size() / 8.0; i++) {
            if (i == page) {
                mb.append("___");
            }
            mb.append(i + " ");
            if (i == page) {
                mb.append("___");
            }
        }
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
