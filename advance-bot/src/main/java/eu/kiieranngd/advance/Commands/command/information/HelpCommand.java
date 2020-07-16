package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

/**
 * Created by kdrew on 09/07/2017.
 */
public class HelpCommand implements Command {

    private Messages messages;
    private Manager manager;

    public HelpCommand(Messages messages, Manager manager) {
        this.messages = messages;
        this.manager = manager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        CGuild cGuild = manager.getGuild(event.getGuild().getId());
        EmbedBuilder eb = new EmbedBuilder();
        if (cGuild != null) {
            if (content.length() == 0) {
                if (event.isFromType(ChannelType.PRIVATE)) {
                    manager.sendPrivateMessage(author, messages.getHelp(cGuild));
                } else {
                    event.getMessage().getChannel().sendMessage(messages.getHelp(cGuild)).queue();
                }
            } else {
                switch (content) {
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"commands", "h"};}

    @Override
    public String getName() {return "help";}

    @Override
    public String getDescription() {return "A list of Commands";}

    @Override
    public String getHelp() {return "help";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
