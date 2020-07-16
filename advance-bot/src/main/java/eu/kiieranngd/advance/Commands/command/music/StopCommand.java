package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 01/06/2017.
 */
public class StopCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public StopCommand(Manager manager, Messages messages, MusicManager musicManager) {
        this.manager = manager;
        this.messages = messages;
        this.musicManager = musicManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        CGuild cGuild = manager.getGuild(event.getGuild().getId());
        Guild guild = event.getGuild();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (cGuild != null) {
            TrackScheduler trackScheduler = musicManager.getMusicManager(cGuild).scheduler;
            AudioPlayer audioPlayer = musicManager.getMusicManager(cGuild).player;
            if (trackScheduler != null && audioPlayer != null) {
                if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    trackScheduler.getQueue().clear();
                    audioPlayer.stopTrack();
                    audioPlayer.setPaused(false);
                    guild.getAudioManager().closeAudioConnection();
                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83C\uDFB5 Playback has been completely stopped and the queue has been cleared.").build()).queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"destroy"};}

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the Bot from Playing and clear the queue";
    }

    @Override
    public String getHelp() {
        return "stop";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
