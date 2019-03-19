package de.DiscordBot.Commands.Music;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import de.DiscordBot.DiscordBot;
import de.DiscordBot.Commands.DiscordCommand;
import de.DiscordBot.Config.Config;
import lavalink.client.io.Lavalink;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class MusicCommand extends DiscordCommand {

    public static HashMap<String, MusicService> guildMusic = new HashMap<String, MusicService>();
    public static Lavalink ll;

    public MusicCommand() {
        super("play", new String[] { "yt", "scloud" }, "Plays music in your current channel",
                "\n\\play [URL]\n\\yt [search|URL]\n\\scloud");
        ll = new Lavalink(DiscordBot.getBot().getSelfUser().getId(), 1, (i) -> {
            return DiscordBot.getBot();
        });
        try {
            ll.addNode(new URI("ws://dev.jectrum.de:18954"), "DVaeBSsWuZ303");
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DiscordBot.getBot().addEventListener(ll);
    }

    @Override
    public Object execute(String command, String[] args, Message m) {
        if (args.length == 0) {
            return new EmbedBuilder().addField(new Field("Usage", getUsage(), true)).build();
        }
        if (m.getGuild().getMember(m.getAuthor()).getVoiceState().getAudioChannel() == null) {
            return new MessageBuilder().append("You need to be in a VoiceChannel to be able to start the Music")
                    .build();
        }
        String search = join(args);
        URL url = null;
        try {
            url = new URL(search);
        } catch (Exception e) {
            if (command.equalsIgnoreCase("play")) {
                return new MessageBuilder().append("Please use ?yt or ?scloud if you want to search with keywords")
                        .build();
            } else if (command.equalsIgnoreCase("yt")) {
                search = "ytsearch:" + search;
            } else if (command.equalsIgnoreCase("scloud")) {
                search = "scsearch:" + search;
            }
        }

        if (!guildMusic.containsKey(m.getGuild().getId())) {
            VoiceChannel toJoin = null;
            OUT: for (VoiceChannel vc : m.getGuild().getVoiceChannels()) {
                for (Member me : vc.getMembers()) {
                    if (me.getUser().getId().equalsIgnoreCase(m.getAuthor().getId())) {
                        toJoin = vc;
                        break OUT;
                    }
                }
            }
            MusicService ms = new MusicService(toJoin, m.getTextChannel());
            ms.queueTrack(search);
            DiscordBot.startService(ms);
            guildMusic.put(m.getGuild().getId(), ms);
        } else {
            guildMusic.get(m.getGuild().getId()).queueTrack(search);
        }
        m.delete().submit();
        return null;
    }

    private String join(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            sb.append(args[i] + " ");
        }
        sb.append(args[args.length - 1]);
        return sb.toString();
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
