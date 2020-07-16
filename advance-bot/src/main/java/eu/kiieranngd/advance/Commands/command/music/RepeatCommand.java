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
 * Created by Kieran on 08/04/2017
 */
public class RepeatCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public RepeatCommand(Manager manager, Messages messages, MusicManager musicManager) {
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
            TrackScheduler trackScheduler = musicManager.getMusicManager(cGuild).scheduler;
            if (trackScheduler != null) {
                if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    trackScheduler.setRepeating(!trackScheduler.isRepeating());
                    event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83C\uDFB5 Player was set to: **" + (trackScheduler.isRepeating() ? "Repeat" : "Not Repeat") + "**").build()).queue();
                } else if (!event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(author)) {
                    event.getChannel().sendMessage(embedBuilder.setDescription("▶ I'm sorry, but you have to be in the same channel as me to use any music related commands").build()).queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{};}

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getDescription() {
        return "Repeats the Queue";
    }

    @Override
    public String getHelp() {
        return "repeat";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
