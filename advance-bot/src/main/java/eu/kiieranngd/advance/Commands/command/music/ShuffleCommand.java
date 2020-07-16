package eu.kiieranngd.advance.Commands.command.music;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Music.TrackScheduler;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by utf_8 on 08/04/2017
 */
public class ShuffleCommand implements Command {

    private Manager manager;
    private MusicManager musicManager;

    public ShuffleCommand(Manager manager, MusicManager musicManager) {
        this.manager = manager;
        this.musicManager = musicManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        CGuild cGuild = manager.getGuild(event.getGuild().getId());

        if (cGuild != null) {
            TrackScheduler trackScheduler = musicManager.getMusicManager(cGuild).scheduler;
            if (trackScheduler != null) {
                if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                    if (trackScheduler.getQueue().isEmpty()) {
                        event.getChannel().sendMessage("\uD83D\uDDD2 The queue is currently empty!").queue();
                        return;
                    }

                    trackScheduler.shuffle();
                    event.getChannel().sendMessage("\uD83C\uDFB5 The queue has been shuffled!").queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"reorder"};}

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "Shuffles the Queue";
    }

    @Override
    public String getHelp() {
        return "shuffle";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
