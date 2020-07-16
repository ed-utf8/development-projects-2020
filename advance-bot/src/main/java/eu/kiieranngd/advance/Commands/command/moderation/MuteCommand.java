package eu.kiieranngd.advance.Commands.command.moderation;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Kieran on 22/04/2017.
 */
public class MuteCommand implements Command {
    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {

    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {return "Adds a Muted Role to the Tagged User";}

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Category getCategory() {
        return null;
    }
}
