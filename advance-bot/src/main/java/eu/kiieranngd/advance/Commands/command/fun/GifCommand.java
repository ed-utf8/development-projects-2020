package eu.kiieranngd.advance.Commands.command.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Utils.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GifCommand implements Command{

    private Config config;
    private Messages messages;

    public GifCommand(Config config, Messages messages) {
        this.config = config;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(messages.getRandomColor())
          .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now());


        try {
            Future<HttpResponse<JsonNode>> future = Unirest.get("https://api.giphy.com/v1/gifs/random?api_key=" + config.getGiphyKey()
                    + ((content == null) ? "" : "&tag=" + URLEncoder.encode(content, "UTF-8"))).asJsonAsync();
            HttpResponse<JsonNode> json = future.get(30, TimeUnit.SECONDS);
            JSONArray data = json.getBody().getObject().getJSONArray("data");
            JSONObject item = data.getJSONObject(0);
            String url = item.getString("image_url");
            eb.setImage(url);

        } catch (InterruptedException | ExecutionException | TimeoutException | UnsupportedEncodingException e) {
            eb.setDescription("I had trouble recieving the Gif \uD83D\uDE2B");
        }
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public String getName() { return "gif"; }

    @Override
    public String getDescription() { return "Show a random gif."; }

    @Override
    public String getHelp() { return "gif"; }

    @Override
    public Category getCategory() { return Category.FUN; }
}
