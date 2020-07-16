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

import java.time.Instant;

/**
 * Created by Kieran on 17/04/2017.
 */
public class InviteCommand implements Command {

    private Messages messages;
    private Manager manager;

    public InviteCommand(Messages messages, Manager manager) {
        this.messages = messages;
        this.manager = manager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(messages.getRandomColor());

        embedBuilder.setAuthor(event.getJDA().getSelfUser().getName(), "https://twitter.com/AdvanceHub", event.getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.addField("❯ Invite", "Want to add me to your Guild: [**Bot Invitation **](https://discordapp.com/oauth2/authorize?&client_id=" + event.getJDA().getSelfUser().getId() + "&scope=botadmin&permissions=8)" + "\nWant to Join my Support Guild: [**Support Invitation**](https://discord.gg/3Ge9HNc)", true);

        embedBuilder.setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now());

        if (message.isFromType(ChannelType.TEXT)) {
            message.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if (message.isFromType(ChannelType.PRIVATE)) {
            manager.sendPrivateMessage(author, embedBuilder.build());
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"inv", "add"};}

    @Override
    public String getName() {return "invite";}

    @Override
    public String getDescription() {return "Invitation Links for Advance";}

    @Override
    public String getHelp() {return "invite";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
