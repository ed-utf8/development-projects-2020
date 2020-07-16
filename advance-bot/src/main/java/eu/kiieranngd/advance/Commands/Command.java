package eu.kiieranngd.advance.Commands;

import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {

    void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception;

    default String[] getAliases() {
        return new String[0];
    }

    String getName();
    String getDescription();
    String getHelp();
    Category getCategory();
}
