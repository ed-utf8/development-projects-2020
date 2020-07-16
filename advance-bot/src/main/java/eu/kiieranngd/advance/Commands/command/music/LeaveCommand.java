package eu.kiieranngd.advance.Commands.command.music;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by utf_8 on 08/04/2017
 */
public class LeaveCommand implements Command {

    private Manager manager;
    private Messages messages;

    public LeaveCommand(Manager manager, Messages messages) {
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        CGuild cGuild = manager.getGuild(event.getGuild().getId());
        if (cGuild != null) {
            if (cGuild.getMods().contains(manager.getUser(event.getAuthor().getId()).getId())) {
                event.getGuild().getAudioManager().setSendingHandler(null);
                event.getGuild().getAudioManager().closeAudioConnection();
                event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83D\uDD34 Left the Voice Channel!").build()).queue();

            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"disconnect"};}

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Make the botadmin leave your Voice Channel";
    }

    @Override
    public String getHelp() {
        return "leave";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }
}
