package eu.kiieranngd.advance.Commands.command;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class CustomCommand implements Command {

    private String response;
    private String command;
    private String description;

    private Manager manager;

    public CustomCommand(String response, String command, String description, Manager manager) {
        this.response = response;
        this.command = command;
        this.description = description;
        this.manager = manager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (message.isFromType(ChannelType.PRIVATE)) {
            manager.sendPrivateMessage(event.getAuthor(), response);
        } else {
            message.getChannel().sendMessage(response).queue();
        }
    }

    @Override
    public String getName() {return command;}

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getHelp() {return null;}

    @Override
    public Category getCategory() {
        return Category.OTHER;
    }
}
