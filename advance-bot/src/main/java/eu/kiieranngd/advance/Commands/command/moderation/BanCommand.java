package eu.kiieranngd.advance.Commands.command.moderation;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;


public class BanCommand implements Command {

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        List<User> users = event.getMessage().getMentionedUsers();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        Guild guild = event.getGuild();

        if (guild.getMember(author).hasPermission(Permission.BAN_MEMBERS)) {
            if (content.length() > 1) {
                for (User user : users) {
                    event.getGuild().getController().ban(user, 7).queue();
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
    public String[] getAliases() {return new String[]{"b"};}

    @Override
    public String getName() {return "ban";}

    @Override
    public String getDescription() {
        return "Ban a Member from the Guild";
    }

    @Override
    public String getHelp() {return "ban @MENTION";}

    @Override
    public Category getCategory() {return Category.MODERATION;}
}

