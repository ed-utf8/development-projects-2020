package eu.kiieranngd.advance.Commands.command.moderation;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Kieran on 09/07/2017.
 */
public class PurgeCommand implements Command {

    private Manager manager;
    public PurgeCommand (Manager manager) {this.manager = manager;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        Guild guild = event.getGuild();

        if (guild.getMember(author).hasPermission(Permission.MESSAGE_MANAGE)) {

            int value;
            try {
                value = Integer.parseInt(content);
            } catch (NumberFormatException exception) {
                channel.sendMessage(embedBuilder.setDescription("❌ That is not a valid number.").build()).queue();
                return;
            }
            if (value < 2) value = 2;
            channel.getHistory().retrievePast(Math.min(value, 100)).queue(messageHistory -> {
                messageHistory = messageHistory.stream().filter(messages -> !messages.getCreationTime()
                        .isBefore(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());

                final int size = messageHistory.size();
                event.getMessage().getTextChannel().deleteMessages(messageHistory).queue(_void -> {
                    channel.sendMessage(embedBuilder.setColor(Color.GREEN).setDescription(size + " messages have been deleted.").build()).queue(messages -> {
                        messages.delete().queueAfter(4, TimeUnit.SECONDS);
                    });
                });
            });
        } else {
            event.getMessage().getChannel().sendMessage(embedBuilder.setDescription("❌ You do not have the `MESSAGE_MANAGE` permission for that " + event.getAuthor().getAsMention()).build()).queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"clear", "prune"};}

    @Override
    public String getName() {return "purge";}

    @Override
    public String getDescription() {return "Purge unwanted messages in text chats";}

    @Override
    public String getHelp() {return "purge <number of messages>";}

    @Override
    public Category getCategory() {return Category.MODERATION;}
}