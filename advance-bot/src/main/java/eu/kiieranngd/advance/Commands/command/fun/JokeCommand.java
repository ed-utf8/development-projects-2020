package eu.kiieranngd.advance.Commands.command.fun;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Utils.WebUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class JokeCommand implements Command {

    private WebUtils webUtils;
    private Messages messages;
    private Map<String, Integer> jokeIndex;

    public JokeCommand(WebUtils webUtils, Messages messages) {
        this.webUtils = webUtils;
        this.messages = messages;
        this.jokeIndex = new TreeMap<>();
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        String guildId = event.getGuild().getId();

        try {
            String rawJSON = webUtils.getText("https://www.reddit.com/r/Jokes/top/.json?sort=top&t=day&limit=400");
            JSONObject jsonObject = new JSONObject(rawJSON);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");

            if(!jokeIndex.containsKey(guildId) || jokeIndex.get(guildId) >= children.length()){
                jokeIndex.put(guildId, 0);
            }

            int jokeI = jokeIndex.get(guildId);

            JSONObject postData = children.getJSONObject(jokeI).getJSONObject("data");

            jokeIndex.put(guildId, jokeI + 1);
            //System.out.println(postData);
            String title = postData.getString("title");
            String text = postData.getString("selftext");
            String url = postData.getString("url");

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(title, url).setDescription(text).setColor(messages.getRandomColor()).build();


        }
        catch (Exception e) {
            channel.sendMessage("The Joke API ain't parsing our freaking JSON \uD83D\uDE26").queue();
            e.printStackTrace();
        }
    }

    @Override
    public String getName() { return "joke"; }

    @Override
    public String getDescription() { return "See a funny joke. Dad's love them!"; }

    @Override
    public String getHelp() { return "joke"; }

    @Override
    public Category getCategory() { return Category.FUN; }
}
