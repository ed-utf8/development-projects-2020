package eu.kiieranngd.advance.Commands.command.fun;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

public class TextToBrickCommand implements Command {

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (content.length() == 0) {
            event.getChannel().sendMessage("Type something to be converted into Brick").queue();
        } else {

            StringBuilder sb = new StringBuilder();
            for (String a : StringUtils.join(content, " ").split("")) {
                if (Character.isLetter(a.toLowerCase().charAt(0))) {
                    sb.append(":regional_indicator_").append(a.toLowerCase()).append(":");
                } else {
                    if (" ".equals(a)) {
                        sb.append(" ");
                    }
                    sb.append(a);
                }
            }
            event.getChannel().sendMessage(sb.toString()).queue();
        }
    }

    @Override
    public String getName() {
        return "ttb";
    }

    @Override
    public String getDescription() {
        return "Convert your writing into Text to Brick";
    }

    @Override
    public String getHelp() {
        return "ttb <Message>";
    }

    @Override
    public Category getCategory() {
        return Category.FUN;
    }
}
