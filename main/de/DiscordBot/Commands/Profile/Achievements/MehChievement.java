package de.DiscordBot.Commands.Profile.Achievements;

import de.DiscordBot.Commands.Profile.Achievements.Types.CounterAchievement;
import net.dv8tion.jda.core.entities.Message;

public class MehChievement extends CounterAchievement {

	public MehChievement() {
		super("mehs", readResources("MehAchievement"));
	}

	@Override
	public boolean processCounter(Message m) {
		String msg = m.getContent().toLowerCase();
		if (!msg.contains("meh")) {
			return false;
		}
		if (msg.length() == 3) {
			return true;
		}
		if (msg.startsWith("meh ")) {
			return true;
		}
		String[] split = msg.split("meh");
		if (split[0].endsWith(" ")) {
			if (split[1].startsWith(" ")) {
				return true;
			}
		}
		return false;
	}

}
