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

/**
 * Created by kdrew on 09/07/2017.
 */
public class AvatarCommand implements Command {

    private Manager manager;
    private Messages messages;

    public AvatarCommand(Manager manager, Messages messages) {
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {

        if (content.length() == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setImage(event.getAuthor().getAvatarUrl());
            embedBuilder.addField(":frame_photo: Avatar", "[Avatar Link](" + event.getAuthor().getAvatarUrl() + ")", true);

            if (event.isFromType(ChannelType.PRIVATE)) {
                manager.sendPrivateMessage(author, embedBuilder.build());
            } else {
                message.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        } else {
            User target = message.getMentionedUsers().get(0);

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(messages.getRandomColor());
            embedBuilder.setImage(target.getAvatarUrl());
            embedBuilder.addField(":frame_photo: Avatar", "[Avatar Link](" + target.getAvatarUrl() + ")", true);

            if (event.isFromType(ChannelType.PRIVATE)) {
                manager.sendPrivateMessage(author, embedBuilder.build());
            } else {
                message.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"picture"};}

    @Override
    public String getName() {return "avatar";}

    @Override
    public String getDescription() {return "Check out a users avatar or your own!";}

    @Override
    public String getHelp() {return "avatar | avatar @MENTION";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
