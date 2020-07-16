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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AsciiCommand implements Command {

    private Messages messages;
    public AsciiCommand (Messages messages) {this.messages = messages;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (content.length() == 0) {
            channel.sendMessage(new EmbedBuilder().setColor(messages.getRandomColor())
                    .setDescription("Here is a **[full list of Ascii Fonts]("+asciiArtUrl+"fonts_list)**. They will be chosen randomly.").build()).queue();
        } else {
            String input = content;
            List<String> fonts = getAsciiFonts();
            String font = fonts.get(randomNum(0, fonts.size() - 1));

            try {
                String ascii = getAsciiArt(input, font);

                if (ascii.length()>1900) {
                    event.getChannel().sendMessage("```fix\n\nThe ascii text is too large ;-;```").queue();
                    return;
                }

                event.getChannel().sendMessage("**Font:** "+font+"\n```fix\n\n"+ascii+"```").queue();
            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage("```fix\n\nYour text contains unknown characters!```").queue();
            }
        }
    }

    @Override
    public String getName() {return "ascii";}

    @Override
    public String getDescription() {return "Have your text, converted into ASCII Art";}

    @Override
    public String getHelp() {return "ascii <Text>";}

    @Override
    public Category getCategory() {return Category.FUN;}

    private String asciiArtUrl = "http://artii.herokuapp.com/";

    private String getAsciiArt(String ascii, String font) {
        try {
            StringBuilder url = new StringBuilder(asciiArtUrl).append("make").append("?text=").append(ascii.replaceAll(" ", "+"))
                    .append(font==null||font.isEmpty()?"":"&font="+font);
            return Unirest.get(url.toString()).asString().getBody();
        } catch (UnirestException e) {
            return "Fail to get the ascii art.";
        }
    }

    public List<String> getAsciiFonts() {
        String url = asciiArtUrl + "fonts_list";
        List<String> fontList = new ArrayList<>();
        try {
            String list = Unirest.get(url).asString().getBody();
            fontList = Arrays.stream(list.split("\n")).collect(Collectors.toList());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        //fontList.forEach(System.out::println);
        return fontList;
    }

    private int randomNum(int start, int end) {
        if(end < start) {
            int temp = end;
            end = start;
            start = temp;
        }
        return (int) Math.floor(Math.random() * (end - start + 1) + start);
    }
}
