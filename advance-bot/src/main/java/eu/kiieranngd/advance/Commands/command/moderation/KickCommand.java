package eu.kiieranngd.advance.Commands.command.moderation;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

/**
 * Created by Kieran on 21/04/2017.
 */
public class KickCommand implements Command {

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        List<User> users = event.getMessage().getMentionedUsers();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        Guild guild = event.getGuild();

        if (guild.getMember(author).hasPermission(Permission.KICK_MEMBERS)) {
            if (content.length() > 1) {
                for (User user : users) {
                    Member member = event.getGuild().getMember(user);
                    event.getGuild().getController().kick(member).queue();
                }
            } else {
                event.getChannel().sendMessage(embedBuilder.setColor(Color.RED).setDescription("❌ You have not mentioned a valid user.").build()).queue();
                return;
            }
        } else {
            event.getMessage().getChannel().sendMessage(embedBuilder.setColor(Color.RED).setDescription("❌ You do not have permission for that " + event.getAuthor().getAsMention()).build()).queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"k"};}

    @Override
    public String getName() {return "kick";}

    @Override
    public String getDescription() {
        return "Kick a Member from the Guild";
    }

    @Override
    public String getHelp() {return "kick @MENTION";}

    @Override
    public Category getCategory() {return Category.MODERATION;}
}