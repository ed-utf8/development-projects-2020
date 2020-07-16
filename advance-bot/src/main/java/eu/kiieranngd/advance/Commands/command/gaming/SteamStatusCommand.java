package eu.kiieranngd.advance.Commands.command.gaming;

import com.mashape.unirest.http.Unirest;
import eu.kiieranngd.advance.Commands.*;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.time.Instant;

public class SteamStatusCommand implements Command {

    private Messages messages;
    public SteamStatusCommand(Messages messages) {this.messages = messages;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        JSONObject result;
        String steamIcon = "http://icons.iconarchive.com/icons/blackvariant/button-ui-requests-10/512/Steam-icon.png";

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(messages.getRandomColor()).setImage(steamIcon)
                .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now());

        try {
            result = Unirest.get("https://steamgaug.es/api/v2").asJson().getBody().getObject();

            //Client
            JSONObject client = result.getJSONObject("ISteamClient");
            String cOnlineStatus = client.get("online").toString();
            if (cOnlineStatus.equals("1")) {
                cOnlineStatus = "<:online:319880156942696449> Online";
            } else {
                cOnlineStatus = "<:offline:319890857899851785> Offline";
            }
            eb.addField("Steam Client", cOnlineStatus, true);

            //Store
            JSONObject store = result.getJSONObject("SteamStore");
            String sOnlineStatus = store.get("online").toString();
            int sResponseTime = (int) store.get("time");
            if (sOnlineStatus.equals("1")) {
                sOnlineStatus = "<:online:319880156942696449> Online";
            } else {
                sOnlineStatus = "<:offline:319890857899851785> Offline";
            }
            eb.addField("Steam Store", "Status: " + sOnlineStatus + "\nPing: " + sResponseTime + "ms", true);

            //Community
            JSONObject steamCommunity = result.getJSONObject("SteamCommunity");
            String sCOnlineStatus = steamCommunity.get("online").toString();
            int sCResponseTime = (int) steamCommunity.get("time");
            if (sCOnlineStatus.equals("1")) {
                sCOnlineStatus = "<:online:319880156942696449> Online";
            } else {
                sCOnlineStatus = "<:offline:319890857899851785> Offline";
            }
            eb.addField("Steam Community", "Status: " + sCOnlineStatus + "\nPing: " + sCResponseTime + "ms", true);

            channel.sendMessage(eb.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();
            channel.sendMessage("There was an error contacting the Steam API. Please try again later").queue();
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"steam", "sstatus"};
    }

    @Override
    public String getName() {
        return "steamstatus";
    }

    @Override
    public String getDescription() {
        return "Steam Server Status";
    }

    @Override
    public String getHelp() {
        return "steamstatus";
    }

    @Override
    public Category getCategory() {
        return Category.GAMING;
    }
}
