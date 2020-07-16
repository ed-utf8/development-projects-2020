package eu.kiieranngd.advance.Commands.command.guildadmin;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Kieran on 09/07/2017.
 */
public class RolesCommand implements Command {

    private Messages messages;

    public RolesCommand(Messages messages) {this.messages = messages;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Role role : channel.getGuild().getRoles()) {
            sb.append(role.getAsMention()).append(" - [").append(role.getId()).append("]\n");
        }

        if (sb.toString().length() < 1024) {

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(event.getGuild().getName() + " Roles")
                    .setColor(messages.getRandomColor())
                    .setDescription(sb.toString());
            event.getTextChannel().sendMessage(embedBuilder.build()).queue();
        } else {

        }
    }

    @Override
    public String getName() {return "roles";}

    @Override
    public String getDescription() {return "Displays the Roles and Role IDs";}

    @Override
    public String getHelp() {return "roles";}

    @Override
    public Category getCategory() {return Category.GUILDADMIN;}
}