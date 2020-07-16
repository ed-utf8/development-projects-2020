package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by utf_8 on 08/04/2017
 */
public class ReplayCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public ReplayCommand(Manager manager, Messages messages, MusicManager musicManager) {
        this.manager = manager;
        this.messages = messages;
        this.musicManager = musicManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        CGuild cGuild = manager.getGuild(event.getGuild().getId());

        if (cGuild != null) {
            AudioPlayer audioPlayer = musicManager.getMusicManager(cGuild).player;
            TrackScheduler trackScheduler = musicManager.getMusicManager(cGuild).scheduler;
            if (audioPlayer != null && trackScheduler != null) {
                if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    AudioTrack track = audioPlayer.getPlayingTrack();
                    if (track == null)
                        track = trackScheduler.getLastTrack();

                    if (track != null) {
                        event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83C\uDFB5 Restarting track: " + track.getInfo().title).build()).queue();
                        audioPlayer.playTrack(track.makeClone());
                    } else {
                        event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83E\uDD37 No track has been previously started, so the player cannot replay a track!").build()).queue();
                    }
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{};}

    @Override
    public String getName() {
        return "replay";
    }

    @Override
    public String getDescription() {
        return "Replay a track";
    }

    @Override
    public String getHelp() {
        return "replay";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
