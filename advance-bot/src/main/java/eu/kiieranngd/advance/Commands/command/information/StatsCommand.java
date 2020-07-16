package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Advance;
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

import java.time.Instant;

public class StatsCommand implements Command {

    private Messages messages;
    private Advance advance;
    private Manager manager;

    public StatsCommand(Messages messages, Manager manager, Advance advance) {
        this.manager = manager;
        this.messages = messages;
        this.advance = advance;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder eb = new EmbedBuilder()
        .setAuthor(event.getJDA().getSelfUser().getName() + " Stats", null, event.getJDA().getSelfUser().getAvatarUrl())
        .setDescription("This is a Summary of Advance's Statistics.")
        .setColor(messages.getRandomColor())
        .addField("Information", "Shards: " + String.valueOf(advance.getSHARD_COUNT()) + "\nGuilds: " + String.valueOf(advance.getTotalGuilds()), true)
        .addField("Commands", "Total Commands: " + String.valueOf(advance.getCommands().size()), true)
        .addField("Users", "Total: " + String.valueOf(advance.getTotalUsers()) + "\nBots: " + String.valueOf(advance.getTotalBots()), true)
        .addField("Channels", "Voice Channels: " + String.valueOf(advance.getTotalVoiceChannels()) + "\nText Channels: " + String.valueOf(advance.getTotalTextChannels()), true)
        .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now());


        if (message.isFromType(ChannelType.PRIVATE)) {
            manager.sendPrivateMessage(author, eb.build());
        } else {
            message.getChannel().sendMessage(eb.build()).queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"statistics"};}

    @Override
    public String getName() {return "stats";}

    @Override
    public String getDescription() {
        return "Statistics of Advance";
    }

    @Override
    public String getHelp() {return "stats";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
