package eu.kiieranngd.advance.Commands.command.fun;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class YoMommaJokeCommand implements Command {

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        event.getTextChannel().sendTyping().queue();

        String joke = new String();
        try {
            joke = (String) Unirest.get("http://api.yomomma.info").asJson().getBody().getObject().get("joke");
        } catch (UnirestException ex) {
            ex.printStackTrace();
            event.getTextChannel().sendMessage("No joke found").queue();
        }
        event.getTextChannel().sendMessage("\uD83D\uDC69 " + String.valueOf(joke)).queue();
    }

    @Override
    public String[] getAliases() {return new String[]{"yomomma", "mommajoke"};}

    @Override
    public String getName() {return "yomomma";}

    @Override
    public String getDescription() {
        return "Post a YoMomma Joke";
    }

    @Override
    public String getHelp() {return "yomomma";}

    @Override
    public Category getCategory() {return Category.FUN;}
}