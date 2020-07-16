package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Music.TrackScheduler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Queue;

/**
 * Created by utf_8 on 08/04/2017
 */
public class QueueCommand implements Command {

    private Manager manager;
    private MusicManager musicManager;
    private Messages messages;

    public QueueCommand(Manager manager, MusicManager musicManager, Messages messages) {
        this.manager = manager;
        this.musicManager = musicManager;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        TrackScheduler trackScheduler = musicManager.getMusicManager(manager.getGuild(event.getGuild().getId())).scheduler;
        if (trackScheduler != null) {
            Queue<AudioTrack> queue = trackScheduler.getQueue();
            synchronized (queue) {
                if (queue.isEmpty()) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    event.getMessage().getChannel().sendMessage(embedBuilder.setDescription("\uD83E\uDD37 The queue is currently empty!").setColor(messages.getRandomColor()).build()).queue();
                } else {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    int trackCount = 0;
                    long queueLength = 0;

                    StringBuilder sb = new StringBuilder();
                    sb.append("\n");
                    for (AudioTrack track : queue) {
                        queueLength += track.getDuration();
                        if (trackCount < 10) {
                            sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ");
                            sb.append(track.getInfo().title).append("\n");
                            trackCount++;
                        }
                    }
                    embedBuilder.addField("\uD83D\uDDD2 Queue Entries: " + queue.size(), sb.toString(), true).setColor(messages.getRandomColor());
                    embedBuilder.setFooter("Total Queue Time Length: " + getTimestamp(queueLength), "https://www.brandeps.com/icon-download/Q/Queue-music-01.svg");

                    event.getMessage().getChannel().sendMessage(embedBuilder.build()).queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"q", "songlist"};}

    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Get the current tracks in the queue";
    }

    @Override
    public String getHelp() {
        return "queue";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
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
