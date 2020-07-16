package eu.kiieranngd.advance.Commands.command.miscellaneous;

import com.google.common.cache.*;
import eu.kiieranngd.advance.Commands.*;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Utils.*;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class WeatherCommand implements Command {

    private Config config;
    private Messages messages;

    public WeatherCommand(Config config, Messages messages) {
        this.config = config;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        try {
            Weather weather = query(content);
            EmbedBuilder eb = new EmbedBuilder()

            .setTitle(weather.getTitle(), null)
            .addField("\uD83C\uDF2C Wind", "**Speed:** " + weather.getWindSpeed() + "m/s", false)
            .addField("\uD83C\uDF25 Atmosphere", "**Humidity:** " + weather.getHumidity() + "%  **Pressure:** " + weather.getPressure(), false)
            .addField("\uD83C\uDFDD Astronomy", "**Sunrise:** " + weather.getSunrise() + "  **Sunset:** " + weather.getSunset(), false)
            .addField("\uD83C\uDF21 Weather", "**Temperature:** " + String.format("%.2f", weather.getTemperatureFarenheit()) + "ºF / " + String.format("%.2f", weather.getTemperatureCelsius()) + "ºC  **Description:** " + weather.getWeatherDescription(), false)
            .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now())
            .setColor(messages.getRandomColor());

            channel.sendMessage(eb.build()).queue();
        } catch (Exception e) {
            channel.sendMessage("Oh well... Something went wrong when querying data!").queue();
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"climate"};
    }

    @Override
    public String getName() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "Queries weather from almost everywhere!";
    }

    @Override
    public String getHelp() {
        return "weather <location>";
    }

    @Override
    public Category getCategory() {return Category.MISCELLANEOUS;}

    private final Cache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(25)
            .build();

    private Weather query(final String name) {
        return new Weather(new JSONObject(cache.asMap().computeIfAbsent(name.toLowerCase(), (n) -> {
        try {
            String URL = "http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(name, "UTF-8") + "&APPID=" + config.getWeatherAPIKey();
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(URL);
            HttpResponse response = client.execute(get);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            return null;
        }
        })));
    }

}
