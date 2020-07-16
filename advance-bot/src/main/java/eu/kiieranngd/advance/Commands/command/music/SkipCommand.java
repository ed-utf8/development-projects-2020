package eu.kiieranngd.advance.Commands.command.music;

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
public class SkipCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public SkipCommand(Manager manager, Messages messages, MusicManager musicManager) {
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
            TrackScheduler trackScheduler = musicManager.getMusicManager(cGuild).scheduler;
            if (trackScheduler != null) {
                if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    trackScheduler.nextTrack();
                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("The current track was skipped.").build()).queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"next"};}

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skip the current track";
    }

    @Override
    public String getHelp() {
        return "skip";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
