package eu.kiieranngd.advance.Commands.command.fun;

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


public class EightBallCommand implements Command {

    private Messages messages;
    private Manager manager;

    public EightBallCommand(Manager manager, Messages messages) {
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(messages.getRandomColor());

        if (content.length() < 0) {
            channel.sendMessage("Ask me a Question. I'm READY!").queue();
        } else {
            embedBuilder.setDescription("\uD83C\uDFB1 \uD83D\uDD2E " + messages.getRandomMessage());

            if (message.isFromType(ChannelType.PRIVATE)) {
                manager.sendPrivateMessage(author, embedBuilder.build());
            } else {
                message.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"eightball"};}

    @Override
    public String getName() {return "8ball";}

    @Override
    public String getDescription() {return "Ask the one with the great wisdom... 8ball";}

    @Override
    public String getHelp() {return "8ball <question>";}

    @Override
    public Category getCategory() {return Category.FUN;}
}