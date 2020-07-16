package eu.kiieranngd.advance.Manager;

import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Manager {

    private Map<String, DiscordUser> userMap = new HashMap<>();

    private List<CGuild> guilds = new ArrayList<>();
    private Map<CGuild, Map<String, Command>> guildCommands = new HashMap<>();

    public DiscordUser getUser(String discordID) {
        return userMap.get(discordID);
    }

    public List<CGuild> getGuilds() {
        return guilds;
    }

    public void addUser(DiscordUser discordUser) {
        userMap.put(discordUser.getDiscordID(), discordUser);
    }

    public CGuild getGuild(long id) {
        for (CGuild guild : guilds) {
            if (guild.getId() == id) {
                return guild;
            }
        }
        return null;
    }

    public CGuild getGuild(String id) {
        for (CGuild guild : guilds) {
            if (guild.getGuildID().equals(id)) {
                return guild;
            }
        }
        return null;
    }

    public void setGuilds(List<CGuild> guilds) {
        this.guilds = guilds;
    }

    public void addGuild(CGuild CGuild) {
        this.guilds.add(CGuild);
    }

    public void sendPrivateMessage(User target, String message) {
        target.openPrivateChannel().queue(c -> c.sendMessage(message).queue());
    }

    public void sendPrivateMessage(User target, MessageEmbed message) {
        target.openPrivateChannel().queue(c -> c.sendMessage(message).queue());
    }

    public Map<CGuild, Map<String, Command>> getGuildCommands() {
        return guildCommands;
    }

    public void addGuildCommands(CGuild cGuild, Map<String, Command> commands) {
        guildCommands.put(cGuild, commands);
    }

    public Map<String, DiscordUser> getUserMap() {
        return userMap;
    }

    public Map<String, Command> getCommands(CGuild cGuild) {
        return guildCommands.get(cGuild);
    }
}
