package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by kdrew on 09/07/2017.
 */
public class PingCommand implements Command {

    private Manager manager;

    public PingCommand (Manager manager) {this.manager = manager;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        OffsetDateTime sentTime = event.getMessage().getCreationTime();
        OffsetDateTime responseTime = OffsetDateTime.now();

        Long pingResponce = Math.abs(sentTime.until(responseTime, ChronoUnit.MILLIS));

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setDescription("\uD83C\uDFD3 Pong " + pingResponce + "ms (" + ratePing(pingResponce) + ")");

        if (message.isFromType(ChannelType.TEXT)) {
            message.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if (message.isFromType(ChannelType.PRIVATE)) {
            manager.sendPrivateMessage(author, embedBuilder.build());
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"latency"};}

    @Override
    public String getName() {return "ping";}

    @Override
    public String getDescription() {return "Gets the current latency of the botadmin";}

    @Override
    public String getHelp() {return "ping";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}

    private String ratePing(long ping) {
        if (ping <= 1) return "supersonic speed! :upside_down:";
        if (ping <= 10) return "faster than Sonic! :smiley:";
        if (ping <= 100) return "great! :smiley:";
        if (ping <= 200) return "nice! :slight_smile:";
        if (ping <= 300) return "decent. :neutral_face:";
        if (ping <= 400) return "average... :confused:";
        if (ping <= 500) return "slightly slow. :slight_frown:";
        if (ping <= 600) return "kinda slow.. :frowning2:";
        if (ping <= 700) return "slow.. :worried:";
        if (ping <= 800) return "too slow. :disappointed:";
        if (ping <= 900) return "bad. :sob: (helpme)";
        if (ping <= 1600) return "#BlameDiscord. :angry:";
        if (ping <= 10000) return "this makes no sense :thinking: #BlameEd";
        return "slow af. :dizzy_face: ";
    }
}
