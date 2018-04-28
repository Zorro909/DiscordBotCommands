package de.DiscordBot.Commands.Music;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordService;
import lavalink.client.io.Link;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MusicService extends DiscordService {

	VoiceChannel play;
	TextChannel updates;
	LinkedBlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<AudioTrack>();
	boolean paused = false;
	long pausedAt = 0;
	boolean stop = false;
	Link l;
	IPlayer ap;
	DefaultAudioPlayerManager dapm;
	
	public MusicService(VoiceChannel toJoin, TextChannel textChannel) {
		l = MusicCommand.ll.getLink(toJoin.getGuild());
		play = toJoin;
		updates = textChannel;
		l.connect(play);
		ap = l.getPlayer();
		MusicCommand.ll.onReady(new ReadyEvent(DiscordBot.getBot(), 0));
		ap.setVolume(4);
		dapm = new DefaultAudioPlayerManager();
		dapm.registerSourceManager(new YoutubeAudioSourceManager(true));
		dapm.registerSourceManager(new SoundCloudAudioSourceManager(true));
		dapm.registerSourceManager(new HttpAudioSourceManager());
	}

	@Override
	public void run() {
		if (!queue.isEmpty()) {
			ap.addListener(new PlayerEventListenerAdapter() {

				@Override
				public void onPlayerPause(IPlayer player) {
					sendMusicPlayer(MusicPlayerState.PAUSED, true);
				}

				@Override
				public void onPlayerResume(IPlayer player) {
					sendMusicPlayer(MusicPlayerState.PLAYING, true);
				}

				@Override
				public void onTrackStart(IPlayer player, AudioTrack track) {
					sendMusicPlayer(MusicPlayerState.PLAYING, true);
				}

				@Override
				public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
					if (endReason == AudioTrackEndReason.LOAD_FAILED) {
						updates.sendMessage(new MessageBuilder()
								.append("Track " + track.getInfo().title + " couldn't be loaded... Skipping").build());
					}
					if (endReason.mayStartNext) {
						if (queue.isEmpty()) {
							stop();
							return;
						}
						try {
							player.playTrack(queue.take());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				@Override
				public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs) {
					updates.sendMessage(new MessageBuilder()
							.append("Track " + track.getInfo().title + " got stuck... Skipping").build());
					if (queue.isEmpty()) {
						stop();
						return;
					}
					try {
						player.playTrack(queue.take());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			play.getGuild().getAudioManager().openAudioConnection(play);
		}
		while ((!queue.isEmpty() || paused || ap.getPlayingTrack() != null) && !stop) {
			if (!paused) {
				AudioTrack at = ap.getPlayingTrack();
				if (at == null) {
					try {
						ap.playTrack(queue.take());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			sendMusicPlayer(paused ? MusicPlayerState.PAUSED : MusicPlayerState.PLAYING);
			try {
				Thread.sleep(6000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void pause() {
		if(!stop) {
		pausedAt = System.currentTimeMillis();
		paused = true;
		ap.setPaused(true);
		}
	}

	public void unpause() {
		if(!stop) {
		paused = false;
		ap.setPaused(false);
		}
	}

	Message last;

	protected void sendMusicPlayer(MusicPlayerState state) {
		sendMusicPlayer(state, false);
	}

	protected void sendMusicPlayer(MusicPlayerState state, boolean forceNew) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Music Player");
		AudioTrack at = ap.getPlayingTrack();
		if (at != null) {
			eb.setAuthor(at.getInfo().author);
			eb.addField("Playing: " + at.getInfo().title,
					String.format("%02d:%02d:%02d / %02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ap.getTrackPosition()),
							TimeUnit.MILLISECONDS.toMinutes(ap.getTrackPosition()) % 60,
							TimeUnit.MILLISECONDS.toSeconds(ap.getTrackPosition()) % 60,
							TimeUnit.MILLISECONDS.toHours(at.getDuration()),
							TimeUnit.MILLISECONDS.toMinutes(at.getDuration()) % 60,
							TimeUnit.MILLISECONDS.toSeconds(at.getDuration()) % 60),
					true);
		} else {
			eb.addField("Playing", "Nothing", true);
		}
		MessageEmbed me = eb.build();
		boolean edited = false;
		if (last != null) {
			if (last.getCreationTime().toLocalDateTime()
					.toEpochSecond(OffsetDateTime.now().getOffset()) < System.currentTimeMillis() - 2 * 60000
					&& !forceNew) {
				last.editMessage(me).submit();
				edited = true;
			}
		}
		if (!edited) {
			if (last != null) {
				last.delete().submit();
			}
			last = updates.sendMessage(me).complete();

			if (state == MusicPlayerState.PAUSED) {
				// Stop
				last.addReaction("\u23F9").queue();
				// Play Button
				last.addReaction("\u25B6").queue();
				// Skip
				last.addReaction("\u23E9").queue();
			} else {
				// Pause
				last.addReaction("\u23F8").queue();
				// Stop
				last.addReaction("\u23F9").queue();
				// Skip
				last.addReaction("\u23E9").queue();
			}
			DiscordBot.registerEmoteChangeListener(last, new ListenerAdapter() {
				@Override
				public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent gmrae) {
					if (gmrae.getReaction().isSelf()) {
						return;
					}
					System.out.println(gmrae.getReactionEmote().getName());
					Message m = gmrae.getChannel().getMessageById(gmrae.getMessageId()).complete();
					Map<Object, Object> map = m.getReactions().stream().collect(Collectors.toMap(r -> ((MessageReaction)r).getReactionEmote().getName(), r -> ((MessageReaction)r).getUsers().complete().stream().filter(u -> {
						if(u.isBot())return false;
						if(play.getGuild().getMember(u).getVoiceState().getAudioChannel()==play) {
							return true;
						}
						return false;
					}).count()));
					double listeners = 0;
					for(Member me : play.getMembers()) {
						if(!me.getUser().isBot()) {
							listeners++;
						}
					}
					
					// STOP					
					if (gmrae.getReactionEmote().getName().equalsIgnoreCase("\u23F9")) {
						try {
							if ((int) map.get("\u23F9") >= listeners / 2.0) {
								stop();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// PAUSE
					} else if (gmrae.getReactionEmote().getName().equalsIgnoreCase("\u23F8")) {
						try {
							if ((int)map.get("\u23F8") >= listeners / 2.0) {
								pause();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// PLAY
					} else if (gmrae.getReactionEmote().getName().equalsIgnoreCase("\u25B6")) {
						try {
							if ((int)map.get("\u25B6") >= listeners / 2.0) {
								unpause();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// SKIP
					} else if (gmrae.getReactionEmote().getName().equalsIgnoreCase("\u23E9")) {
						try {
							if ((int)map.get("\u23E9") - 1 >= listeners / 2.0) {
								if (!skipTrack()) {
									updates.sendMessage("Could not skip Track, maybe the queue is empty?").submit();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}, 5 * 60 * 1000);
		}
	}

	protected boolean skipTrack() {
		try {
			if(queue.isEmpty()) {
				return false;
			}
			ap.playTrack(queue.take());
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	protected void stop() {
		try {
		if(ap.getPlayingTrack()!=null)ap.stopTrack();
		ap.playTrack(null);
		}catch(Exception e) {}
		l.disconnect();
		
		stop = true;
		MusicCommand.guildMusic.remove(play.getGuild().getId());
	}

	@Override
	public void shutdown() {
		stop();
	}

	public void queueTrack(final String search) {
		try {
			dapm.loadItem(search, new AudioLoadResultHandler() {

				@Override
				public void trackLoaded(AudioTrack track) {
					try {
						queue.put(track);
						updates.sendMessage("Added the song " + track.getInfo().title).submit();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					if (search.startsWith("ytsearch:") || search.startsWith("scsearch:")) {
						trackLoaded(playlist.getTracks().get(0));
						return;
					}
					int tracks = 0;
					Collections.shuffle(playlist.getTracks());
					for (AudioTrack at : playlist.getTracks()) {
						try {
							queue.put(at);
							tracks++;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					updates.sendMessage("Added " + tracks + " songs").submit();
				}

				@Override
				public void noMatches() {
					updates.sendMessage("Nothing found for search '" + search + "'").submit();
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					if (exception.severity == Severity.COMMON) {
						updates.sendMessage(
								"Source for '" + search + "' is probably blocked, please try something else").submit();
					} else {
						updates.sendMessage("The Song could not be loaded... Contact @Zorro909#1972").submit();
					}
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
