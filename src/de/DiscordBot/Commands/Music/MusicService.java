package de.DiscordBot.Commands.Music;

import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordService;
import javautils.UtilHelpers.Cleanable;
import net.dv8tion.jda.client.managers.EmoteManager;
import net.dv8tion.jda.client.managers.EmoteManagerUpdatable;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.AuditableRestAction;

public class MusicService extends DiscordService {

	VoiceChannel play;
	TextChannel updates;
	LinkedBlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<AudioTrack>();
	DefaultAudioPlayerManager dapm;
	AudioPlayer ap;
	boolean paused = false;
	long pausedAt = 0;
	boolean stop = false;

	public MusicService(VoiceChannel toJoin, TextChannel textChannel) {
		play = toJoin;
		updates = textChannel;
		dapm = new DefaultAudioPlayerManager();
		ap = dapm.createPlayer();
		dapm.registerSourceManager(new YoutubeAudioSourceManager(true));
		dapm.registerSourceManager(new SoundCloudAudioSourceManager(true));
		dapm.registerSourceManager(new com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager());
	}

	@Override
	public void run() {
		if (!queue.isEmpty()) {
			ap.addListener(new AudioEventAdapter() {

				@Override
				public void onPlayerPause(AudioPlayer player) {
					sendMusicPlayer(MusicPlayerState.PAUSED, true);
				}

				@Override
				public void onPlayerResume(AudioPlayer player) {
					sendMusicPlayer(MusicPlayerState.PLAYING, true);
				}

				@Override
				public void onTrackStart(AudioPlayer player, AudioTrack track) {
					sendMusicPlayer(MusicPlayerState.PLAYING, true);
				}

				@Override
				public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
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
				public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
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
			play.getGuild().getAudioManager().setSendingHandler(new AudioSendHandler() {
				AudioFrame f;

				@Override
				public byte[] provide20MsAudio() {
					return f.data;
				}

				@Override
				public boolean canProvide() {
					f = ap.provide();
					return f != null;
				}
			});
			play.getGuild().getAudioManager().openAudioConnection(play);
		}
		while ((!queue.isEmpty() || paused) && !stop) {
			AudioTrack at = ap.getPlayingTrack();
			if (at == null) {
				try {
					ap.playTrack(queue.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		pausedAt = System.currentTimeMillis();
		paused = true;
		ap.setPaused(true);
	}

	public void unpause() {
		paused = false;
		ap.setPaused(false);
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
			eb.addField("Playing: " + at.getInfo().title, String.format("%d:%d:%d / %d:%d:%d",
					TimeUnit.MILLISECONDS.toHours(at.getPosition()), TimeUnit.MILLISECONDS.toMinutes(at.getPosition()),
					TimeUnit.MILLISECONDS.toSeconds(at.getPosition()), TimeUnit.MILLISECONDS.toHours(at.getDuration()),
					TimeUnit.MILLISECONDS.toMinutes(at.getDuration()),
					TimeUnit.MILLISECONDS.toSeconds(at.getDuration())), true);
		} else {
			eb.addField("Playing", "Nothing", true);
		}
		MessageEmbed me = eb.build();
		boolean edited = false;
		if (last != null) {
			if (last.getCreationTime().toLocalDateTime().toEpochSecond(
					ZoneOffset.of(ZoneOffset.systemDefault().getId())) < System.currentTimeMillis() - 2 * 60000
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
				last.addReaction("\u23F9").complete();
				// Play Button
				last.addReaction("\u23F5").complete();
				// Skip
				last.addReaction("\u23E9").complete();
			} else {
				// Pause
				last.addReaction("\u23F8").complete();
				// Stop
				last.addReaction("\u23F9").complete();
				// Skip
				last.addReaction("\u23E9").complete();
			}
			DiscordBot.registerEmoteChangeListener(last, new ListenerAdapter() {
				@Override
				public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent gmrae) {
					if (gmrae.getReaction().isSelf()) {
						return;
					}
					if (gmrae.getReaction().getReactionEmote().getEmote().getName().equalsIgnoreCase("stop_button")) {
						try {
							if (gmrae.getGuild().getMember(gmrae.getUser()).hasPermission(Permission.ADMINISTRATOR)
									|| gmrae.getReaction().getCount() - 1 >= (double) play.getMembers().size() / 2.0) {
								stop();
							}
						} catch (Exception e) {
						}
					} else if (gmrae.getReaction().getReactionEmote().getEmote().getName()
							.equalsIgnoreCase("pause_button")) {
						try {
							if (gmrae.getGuild().getMember(gmrae.getUser()).hasPermission(Permission.ADMINISTRATOR)
									|| gmrae.getReaction().getCount() - 1 >= (double) play.getMembers().size() / 2.0) {
								pause();
							}
						} catch (Exception e) {
						}
					} else if (gmrae.getReaction().getReactionEmote().getEmote().getName()
							.equalsIgnoreCase("arrow_forward")) {
						try {
							if (gmrae.getGuild().getMember(gmrae.getUser()).hasPermission(Permission.ADMINISTRATOR)
									|| gmrae.getReaction().getCount() - 1 >= (double) play.getMembers().size() / 2.0) {
								unpause();
							}
						} catch (Exception e) {
						}
					} else if (gmrae.getReaction().getReactionEmote().getEmote().getName()
							.equalsIgnoreCase("fast_forward")) {
						try {
							if (gmrae.getGuild().getMember(gmrae.getUser()).hasPermission(Permission.ADMINISTRATOR)
									|| gmrae.getReaction().getCount() - 1 >= (double) play.getMembers().size() / 2.0) {
								if (!skipTrack()) {
									updates.sendMessage("Could not skip Track, maybe the queue is empty?").submit();
								}
							}
						} catch (Exception e) {
						}
					}
				}

			}, 4 * 60 * 1000);
		}
	}

	protected boolean skipTrack() {
		try {
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
		ap.stopTrack();
		play.getGuild().getAudioManager().closeAudioConnection();
		stop = true;
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
