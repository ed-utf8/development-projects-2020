package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.ConfigManager;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Music.GuildMusicManager;
import eu.kiieranngd.advance.Music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.net.URL;
import java.util.List;

/**
 * Created by kierandrew on 20/07/2017.
 */
public class SoundcloudSearch implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public SoundcloudSearch(Manager manager, Messages messages, MusicManager musicManager) {
        this.manager = manager;
        this.messages = messages;
        this.musicManager = musicManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        CGuild cGuild = manager.getGuild(event.getGuild().getId());
        ConfigManager configManager = cGuild.getConfigManager();

        GuildMusicManager guildMusicManager = musicManager.getMusicManager(cGuild);
        TrackScheduler trackScheduler = guildMusicManager.scheduler;
        AudioPlayer audioPlayer = guildMusicManager.player;
        AudioPlayerManager audioPlayerManager = musicManager.getPlayerManager();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(messages.getRandomColor());

        if (cGuild != null) {
            if (configManager != null) {
                if (guildMusicManager != null) {
                    if (trackScheduler != null && audioPlayer != null && audioPlayerManager != null) {
                        if (content.length() == 0) {
                            if (!event.getGuild().getAudioManager().isConnected()) {
                                event.getChannel().sendMessage(embedBuilder.setDescription("▶ I'm not in a voice channel, Join a channel then request a song using " + configManager.getPrefix() + "play <song name / link> and I'll run the Party").build()).queue();
                            } else if (!event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())) {
                                event.getChannel().sendMessage(embedBuilder.setDescription("▶ I'm sorry, but you have to be in the same channel as me to use any music related commands").build()).queue();
                            } else if (audioPlayer.isPaused()) {
                                audioPlayer.setPaused(false);
                                event.getChannel().sendMessage(embedBuilder.setDescription("▶ Playback as been resumed.").build()).queue();
                            } else if (audioPlayer.getPlayingTrack() != null) {
                                event.getChannel().sendMessage(embedBuilder.setDescription("\uD83C\uDFB5 Player is already playing!").build()).queue();
                            } else if (trackScheduler.getQueue().isEmpty()) {
                                event.getChannel().sendMessage(embedBuilder.setDescription("\uD83D\uDDD2 The current audio queue is empty! Add something to the queue first!").build()).queue();
                            }
                        } else {
                            loadAndPlay(guildMusicManager, content, audioPlayerManager, event);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"soundcloudsearch", "scplay"};}

    @Override
    public String getName() {return "scsearch";}

    @Override
    public String getDescription() {return "Searches Soundcloud for the Selected Song";}

    @Override
    public String getHelp() {return "scsearch <term or url>";}

    @Override
    public Category getCategory() {return Category.MUSIC;}

    private void loadAndPlay(GuildMusicManager mng, String content, AudioPlayerManager audioPlayerManager, MessageReceivedEvent event) {

        try {
            new URL(content);
        } catch(Exception e){
            content = "scsearch:" + content;
        }

        String finalContent = content;
        audioPlayerManager.loadItemOrdered(mng, content, new AudioLoadResultHandler() {

            EmbedBuilder embedBuilder = new EmbedBuilder();

            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "\uD83C\uDFB5 " + event.getAuthor().getAsMention() +" Adding to queue: " + track.getInfo().title;

                for (VoiceChannel voiceChannel : event.getGuild().getVoiceChannels()) {
                    if (voiceChannel.getMembers().contains(event.getMember())) {
                        event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                        event.getGuild().getAudioManager().setSendingHandler(mng.getSendHandler());
                    }
                }

                mng.scheduler.queue(track);
                event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription(msg).build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();

                for (VoiceChannel voiceChannel : event.getGuild().getVoiceChannels()) {
                    if (voiceChannel.getMembers().contains(event.getMember())) {
                        event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                        event.getGuild().getAudioManager().setSendingHandler(mng.getSendHandler());
                    }
                }

                if(playlist.isSearchResult() || playlist.getSelectedTrack()!=null) {

                    AudioTrack single = playlist.getTracks().get(0);
                    String msg = "\uD83C\uDFB5 " + event.getAuthor().getAsMention() +" Adding to queue: " + single.getInfo().title;
                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription(msg).build()).queue();
                    mng.scheduler.queue(single);

                } else {

                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83C\uDFB5 Adding **" + playlist.getTracks().size() +"** tracks to queue from your playlist: " + playlist.getName()).build()).queue();
                    tracks.forEach(mng.scheduler::queue);
                }
            }


            @Override
            public void noMatches() {
                event.getChannel().sendMessage(embedBuilder.setColor(Color.RED).setDescription("\uD83E\uDD37 Nothing found by " + finalContent).build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83D\uDD34 Could not play: " + exception.getMessage()).build()).queue();
            }
        });
    }

    private String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}
