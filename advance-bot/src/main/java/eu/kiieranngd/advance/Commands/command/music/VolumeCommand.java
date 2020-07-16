package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by utf_8 on 08/04/2017
 */
public class VolumeCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public VolumeCommand(Manager manager, Messages messages, MusicManager musicManager) {
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
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (cGuild != null) {
            AudioPlayer audioPlayer = musicManager.getMusicManager(cGuild).player;
            if (audioPlayer != null) {
                if (content.length() < 1) {
                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription(volumeIcon(audioPlayer.getVolume()) + " Current player volume: **" + audioPlayer.getVolume() + "**").build()).queue();
                } else if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    try {
                        int newVolume = Math.max(10, Math.min(100, Integer.parseInt(content)));
                        int oldVolume = audioPlayer.getVolume();
                        audioPlayer.setVolume(newVolume);
                        event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription(volumeIcon(newVolume) + " Player volume changed from `" + oldVolume + "` to `" + newVolume + "`").build()).queue();
                    } catch (NumberFormatException e) {
                        event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83D\uDD34 `" + String.valueOf(content) + "` is not a valid integer. (10 - 100)").build()).queue();
                    }
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"vol"};}

    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getDescription() {
        return "Change the volume of the botadmin";
    }

    @Override
    public String getHelp() {
        return "volume";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }

    public String volumeIcon(int volume) {
        if(volume == 10)
            return "\uD83D\uDD08";
        if(volume < 30)
            return "\uD83D\uDD08";
        if(volume < 70)
            return "\uD83D\uDD09";
        return "\uD83D\uDD0A";
    }
}
