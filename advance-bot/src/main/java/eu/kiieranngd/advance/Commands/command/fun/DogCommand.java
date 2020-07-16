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

public class DogCommand implements Command {

    private Messages messages;
    public DogCommand(Messages messages) {this.messages = messages;
    }
    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        String dogImage = new String();
        try {
            dogImage = (String) Unirest.get("https://random.dog/woof.json").asJson().getBody().getObject().get("url");
        } catch (UnirestException ex) {
            ex.printStackTrace();
            event.getTextChannel().sendMessage("\uD83D\uDC36 Can't Load any Cats").queue();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("\uD83D\uDC36 DOG")
                .setImage(dogImage)
                .setColor(messages.getRandomColor());
        channel.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getName() {return "dog";}

    @Override
    public String getDescription() {return "Wanna see some Dogs?";}

    @Override
    public String getHelp() {return "dog";}

    @Override
    public Category getCategory() {return Category.FUN;}
}
