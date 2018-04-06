package de.DiscordBot.Commands;

import java.util.HashMap;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Config.Config;
import de.DiscordBot.Config.ConfigPage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TicTacToeCommand extends DiscordCommand implements EventListener {

	public TicTacToeCommand() {
		super("tictactoe", new String[] {}, "Let's you play a round of tic tac toe against someone else",
				"\\tictactoe @Opponent");
		DiscordBot.getBot().addEventListener(this);
	}

	@Override
	public Object execute(String command, String[] args, Message m) {
		if (state.containsKey(m.getChannel())) {
			return new MessageBuilder().append(
					"Sorry but in this Channel there is already a Game playing, finish it before starting a new one!")
					.build();
		} else {
			Game g = new Game();
			g.channel = m.getChannel();
			g.players[0] = m.getGuild().getMember(m.getAuthor());
			g.players[1] = m.getGuild().getMember(m.getMentionedUsers().get(0));
			g.lastPlayer = Math.random() < 0.5 ? 0 : 1;

			Message msg = new MessageBuilder().append(
					g.players[1].getAsMention() + " You were challenged to a game of Tic Tac Toe\nDo you accept?")
					.build();
			g.lastId = m.getChannel().sendMessage(msg).complete().getId();
			msg.addReaction("\u1F44D").complete();
			msg.addReaction("\u1F44E").complete();
			DiscordBot.registerEmoteChangeListener(msg, new ListenerAdapter() {
				@Override
				public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent gmrae) {
					if (g.accepted = true)
						return;
					if (gmrae.getMember().getUser().getId().equals(g.players[1])) {
						if (gmrae.getReactionEmote().getName().equals("\u1F44D")) {
							g.accepted = true;
							sendBoard(g);
						} else if (gmrae.getReactionEmote().getName().equals("\u1F44E")) {
							g.accepted = false;
							msg.delete().submit();
							m.getChannel()
									.sendMessage(
											m.getAuthor().getAsMention() + " The Game Invitation got denied, sry :cry:")
									.submit();
							state.remove(m.getChannel());
						}
					}
				}
			});
			state.put(m.getChannel(), g);
			return "";
		}
	}

	protected void sendBoard(Game g) {
		MessageBuilder msg = new MessageBuilder();

		msg.append("Tic Tac Toe\n");
		msg.append(g.players[0].getNickname() + " vs " + g.players[1].getNickname() + "\n");
		msg.append("To select a tile, write it's number in the chat\n");
		int i = 1;
		for (String[] row : g.board) {
			for (String column : row) {
				int l = Integer.decode("0x003" + i);
				msg.append((column == null) ? String.valueOf(Character.toChars(l)) + "\u20E3" : column);
				i++;
			}
			msg.append("\n");
		}
		String wonEmote = "";
		for (int l = 0; l < 3; l++) {
			if (g.board[l][0] != null && g.board[l][1] != null && g.board[l][2] != null) {
				if (g.board[l][0].equals(g.board[l][1]) && g.board[l][1].equals(g.board[l][2])) {
					wonEmote = g.board[l][0];
				}
			}
			if (g.board[0][l] != null && g.board[1][l] != null && g.board[2][l] != null) {
				if (g.board[0][l].equals(g.board[1][l]) && g.board[1][l].equals(g.board[2][l])) {
					wonEmote = g.board[0][l];
				}
			}
		}
		if (g.board[0][0] != null && g.board[1][1] != null && g.board[2][2] != null) {
			if (g.board[0][0].equals(g.board[1][1]) && g.board[1][1].equals(g.board[2][2])) {
				wonEmote = g.board[1][1];
			}
		}
		if (g.board[0][2] != null && g.board[1][1] != null && g.board[2][0] != null) {
			if (g.board[0][2].equals(g.board[1][1]) && g.board[1][1].equals(g.board[2][0])) {
				wonEmote = g.board[1][1];
			}
		}
		if (!wonEmote.isEmpty()) {
			state.remove(g.channel);
			String winner = "";
			if (wonEmote.equals(g.emojis[0])) {
				winner = g.players[0].getAsMention();
			} else {
				winner = g.players[1].getAsMention();
			}
			msg.append(winner + " has won the game!\nCongratulations :clap:");
		}
		OUT: if (wonEmote.isEmpty()) {
			for (i = 0; i < 3; i++) {
				for (int i2 = 0; i2 < 3; i2++) {
					if (g.board[i][i2] == null)
						break OUT;
				}
				if (i == 2) {
					msg.append("Nobody won... It's a tie :cry:");
				}
			}
		}
		g.channel.sendMessage(msg.build()).queue();
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

	HashMap<MessageChannel, Game> state = new HashMap<MessageChannel, Game>();

	private class Game {
		String lastId;
		MessageChannel channel;
		String[][] board = new String[3][3];
		Member[] players = new Member[2];
		String[] emojis = new String[] { "\u2716", "\u2B55" };
		int lastPlayer;
		boolean accepted = false;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof MessageReceivedEvent) {
			MessageReceivedEvent mre = (MessageReceivedEvent) event;
			if (mre.getMessage().getContent().length() == 1) {
				if (state.containsKey(mre.getChannel())) {
					Game g = state.get(mre.getChannel());
					if (g.accepted) {
						int cPlayer = 0;
						if (g.lastPlayer == 0) {
							cPlayer = 1;
						}
						if (g.players[cPlayer].getUser().getId().equals(mre.getAuthor().getId())) {
							int l = 0;
							try {
								l = Integer.parseInt(mre.getMessage().getContent()) - 1;
							} catch (Exception e) {
								return;
							}
							if (l > 8 || l < 0) {
								mre.getChannel().sendMessage("The tiles are numbered 1 through 9!").queue();
								return;
							}
							if (g.board[Math.round(l / 3)][l % 3] != null) {
								mre.getChannel().sendMessage("The tile " + (l + 1) + " was already chosen!");
								return;
							}
							mre.getChannel().getMessageById(g.lastId).complete().delete().queue();
							g.board[Math.round(l / 3)][l % 3] = g.emojis[cPlayer];
							g.lastPlayer = cPlayer;
							sendBoard(g);
						}
					}
				}
			}
		}
	}

}
