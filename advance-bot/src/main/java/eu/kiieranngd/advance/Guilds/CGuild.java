package eu.kiieranngd.advance.Guilds;

import eu.kiieranngd.advance.Manager.ConfigManager;

import java.util.List;

public class CGuild {

    private long id;

    private String guildID;

    private List<Long> admins;
    private List<Long> mods;
    private List<Long> discordUsers;

    private ConfigManager configManager;

    public CGuild(long id, String guildID, List<Long> admins, List<Long> mods, List<Long> discordUsers, ConfigManager configManager) {
        this.id = id;
        this.guildID = guildID;
        this.admins = admins;
        this.mods = mods;
        this.discordUsers = discordUsers;
        this.configManager = configManager;
    }

    public long getId() {
        return id;
    }

    public String getGuildID() {
        return guildID;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public List<Long> getAdmins() {
        return admins;
    }

    public List<Long> getMods() {
        return mods;
    }

    public void addAdmin(long userID) {
        admins.add(userID);
    }

    public void addMod(long userID) {
        mods.add(userID);
    }

    public void removeAdmin(long userID) {
        admins.remove(userID);
    }

    public void removeMod(long userID) {
        mods.remove(userID);
    }

    public List<Long> getDiscordUsers() {
        return discordUsers;
    }
}
