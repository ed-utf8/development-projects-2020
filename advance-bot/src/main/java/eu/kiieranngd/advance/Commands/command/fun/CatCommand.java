package eu.kiieranngd.advance.Commands.command.fun;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CatCommand implements Command {

    private Messages messages;
    public CatCommand(Messages messages) {this.messages = messages;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        String catImage = new String();
        try {
            catImage = (String) Unirest.get("http://random.cat/meow").asJson().getBody().getObject().get("file");
        } catch (UnirestException ex) {
            ex.printStackTrace();
            event.getTextChannel().sendMessage("\uD83D\uDC31 Can't Load any Cats").queue();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("\uD83D\uDC31 CAT")
                    .setImage(catImage)
                    .setColor(messages.getRandomColor());
        channel.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getName() {return "cat";}

    @Override
    public String getDescription() {return "Wanna see some cats?";}

    @Override
    public String getHelp() {return "cat";}

    @Override
    public Category getCategory() {return Category.FUN;}
}
