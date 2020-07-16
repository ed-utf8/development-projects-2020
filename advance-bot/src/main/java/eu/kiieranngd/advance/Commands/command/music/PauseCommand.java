package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.ConfigManager;
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
public class PauseCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public PauseCommand(Manager manager, Messages messages, MusicManager musicManager) {
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
        ConfigManager configManager = cGuild.getConfigManager();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (cGuild != null) {
            TrackScheduler trackScheduler = musicManager.getMusicManager(cGuild).scheduler;
            AudioPlayer audioPlayer = musicManager.getMusicManager(cGuild).player;
            if (trackScheduler != null && audioPlayer != null) {
                if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    audioPlayer.setPaused(true);
                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\u23F8 Playback has been paused.").build()).queue();
                } else if (!event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(author)) {
                    event.getChannel().sendMessage(embedBuilder.setDescription("▶ I'm sorry, but you have to be in the same channel as me to use any music related commands").build()).queue();
                    if (!event.getGuild().getAudioManager().isConnected()) {
                        event.getChannel().sendMessage(embedBuilder.setDescription("▶ I'm not in a voice channel, request a song using " + configManager.getPrefix() + "play <song name / link> to make me join a channel").build()).queue();
                    } else if (!event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getAuthor())) {
                        event.getChannel().sendMessage(embedBuilder.setDescription("▶ I'm sorry, but you have to be in the same channel as me to use any music related commands").build()).queue();
                    } else if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                        audioPlayer.setPaused(true);
                        event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\u23F8 Playback has been paused.").build()).queue();
                    }
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"pausesong"};}

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getDescription() {
        return "Pauses the Playback";
    }

    @Override
    public String getHelp() {
        return "pause";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
