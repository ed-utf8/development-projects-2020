package eu.kiieranngd.advance.Commands.command.miscellaneous;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LMGTFYCommand implements Command {

    private Messages messages;
    public LMGTFYCommand(Messages messages) {this.messages = messages;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        String url = "http://lmgtfy.com/?q=";
        String image = "https://lmgtfy.com/assets/sticker-b222a421fb6cf257985abfab188be7d6746866850efe2a800a3e57052e1a2411.png";

        if (content.length() == 0) {
            channel.sendMessage("Please Enter a Term to Let Me Google that for you :shrug:").queue();
        } else {
            channel.sendMessage(new EmbedBuilder().setDescription("Here is your Requested Term: [Click Here]("+ url + content+ ")")
                    .setColor(messages.getRandomColor()).setThumbnail(image).build()).queue();
        }
    }

    @Override
    public String getName() {
        return "lmgtfy";
    }

    @Override
    public String getDescription() {
        return "Let me Google that for you";
    }

    @Override
    public String getHelp() {
        return "lmgtfy <search term>";
    }

    @Override
    public Category getCategory() {
        return Category.MISCELLANEOUS;
    }
}
