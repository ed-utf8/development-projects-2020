package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

/**
 * Created by Kieran on 20/05/2017.
 */
public class UptimeCommand implements Command {

    private Messages messages;
    private Manager manager;
    private long startTime;

    public UptimeCommand(Messages messages, Manager manager, long startTime) {
        this.messages = messages;
        this.manager = manager;
        this.startTime = startTime;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        long upTime = System.currentTimeMillis() - startTime;
        long inSeconds = TimeUnit.MILLISECONDS.toSeconds(upTime);

        embedBuilder.setTitle("Advance Uptime", "https://twitter.com/advancehub")
                .setColor(messages.getRandomColor())
                .setDescription("⏱ " + messages.getFormattedIntTime(inSeconds));

        if (message.isFromType(ChannelType.TEXT)) {
            message.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if (message.isFromType(ChannelType.PRIVATE)) {
            manager.sendPrivateMessage(author, embedBuilder.build());
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"onlinetime"};}

    @Override
    public String getName() {return "uptime";}

    @Override
    public String getDescription() {
        return "Session Uptime of Advance";
    }

    @Override
    public String getHelp() {return "uptime";}

    @Override
    public Category getCategory() {
        return Category.INFORMATION;
    }
}
