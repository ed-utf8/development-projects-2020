package eu.kiieranngd.advance.Commands.command.fun;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UrbanCommand implements Command {

    private Manager manager;

    public UrbanCommand(Manager manager) {this.manager = manager;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (content.length() >= 1) {
            String cool = content;
            try {
                Future<HttpResponse<JsonNode>> future = Unirest.get("https://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(cool, "UTF-8")).asJsonAsync();
                HttpResponse<JsonNode> json = future.get(30, TimeUnit.SECONDS);
                JSONArray list = json.getBody().getObject().getJSONArray("list");
                if (list.length() != 0) {
                    JSONObject item = list.getJSONObject(0);
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    String definition = item.getString("definition");
                    if ((definition.length() / 1024) <= 1) {
                        if (event.isFromType(ChannelType.PRIVATE)) {
                            manager.sendPrivateMessage(event.getAuthor(), embedBuilder
                                    .setAuthor("Urban Dictionary - " + cool, item.getString("permalink"), "https://cdn.discordapp.com/attachments/299966385713315840/301271987890683904/eyJ1cmwiOiJodHRwczovL2VyaXNib3QuY29tL2Fzc2V0cy9lbW9qaXMvdXJiYW5kaWN0aW9uYXJ5LnBuZyJ9.png")
                                    .addField("❯ Definition", item.getString("definition"), true)
                                    .addField("❯ Example", item.getString("example"), true)
                                    .addField("❯ Author", item.getString("author"), true)
                                    .addField("❯ Thumbs Up", "\uD83D\uDC4D " + item.getLong("thumbs_up"), true)
                                    .addField("❯ Thumbs Down", "\uD83D\uDC4E " + item.getLong("thumbs_down"), true)
                                    .setColor(Color.YELLOW).build());
                        } else {
                            event.getMessage().getChannel().sendMessage(embedBuilder
                                    .setAuthor("Urban Dictionary - " + cool, item.getString("permalink"), "https://cdn.discordapp.com/attachments/299966385713315840/301271987890683904/eyJ1cmwiOiJodHRwczovL2VyaXNib3QuY29tL2Fzc2V0cy9lbW9qaXMvdXJiYW5kaWN0aW9uYXJ5LnBuZyJ9.png")
                                    .addField("❯ Definition", item.getString("definition"), true)
                                    .addField("❯ Example", item.getString("example"), true)
                                    .addField("❯ Author", item.getString("author"), true)
                                    .addField("❯ Thumbs Up", "\uD83D\uDC4D " + item.getLong("thumbs_up"), true)
                                    .addField("❯ Thumbs Down", "\uD83D\uDC4E " + item.getLong("thumbs_down"), true)
                                    .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now())
                                    .setColor(Color.YELLOW).build()).queue();
                        }
                    } else {
                        embedBuilder
                                .setAuthor("Urban Dictionary - " + cool, item.getString("permalink"), "https://cdn.discordapp.com/attachments/299966385713315840/301271987890683904/eyJ1cmwiOiJodHRwczovL2VyaXNib3QuY29tL2Fzc2V0cy9lbW9qaXMvdXJiYW5kaWN0aW9uYXJ5LnBuZyJ9.png")
                                .addField("Link", "Due to the response being too long, here is the direct link to view it on Urban Dictionary's Website. [Click Here]("+ item.getString("permalink")+")", true)
                                .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now())
                                .setThumbnail("https://cdn.discordapp.com/attachments/299966385713315840/301271987890683904/eyJ1cmwiOiJodHRwczovL2VyaXNib3QuY29tL2Fzc2V0cy9lbW9qaXMvdXJiYW5kaWN0aW9uYXJ5LnBuZyJ9.png")
                                .setColor(Color.YELLOW);

                        if (event.isFromType(ChannelType.PRIVATE))
                            manager.sendPrivateMessage(event.getAuthor(), embedBuilder.build());
                        else
                            event.getMessage().getChannel().sendMessage(embedBuilder.build()).queue();
                    }
                } else {
                    if (event.isFromType(ChannelType.PRIVATE)) {
                        manager.sendPrivateMessage(event.getAuthor(), "Sorry, I couldn't find that word!");
                    } else {
                        event.getMessage().getChannel().sendMessage("Sorry, I couldn't find that word!").queue();
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            channel.sendMessage("Please Enter a Term to Search for on UrbanDictionary.com").queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"ud", "urbandictionary"};}

    @Override
    public String getName() {return "urban";}

    @Override
    public String getDescription() {return "Get an 'urban' perspective?";}

    @Override
    public String getHelp() {return "urban";}

    @Override
    public Category getCategory() {return Category.FUN;}
}