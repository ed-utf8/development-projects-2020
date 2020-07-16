package eu.kiieranngd.advance.User;

import eu.kiieranngd.advance.Guilds.CGuild;

import java.util.ArrayList;
import java.util.List;


public class DiscordUser {

    private long id;
    private long balance = 0;

    private String discordID;

    private List<CGuild> guilds;

    private boolean donator = false;
    private boolean admin = false;
    private boolean blacklisted = false;

    public DiscordUser(long id, String discordID, List<CGuild> guilds, long balance, boolean donator, boolean admin, boolean blacklisted) {
        this.id = id;
        this.discordID = discordID;
        this.guilds = guilds;
        this.balance = balance;
        this.donator = donator;
        this.admin = admin;
        this.blacklisted = blacklisted;
    }

    public DiscordUser(long id, String discordID, CGuild guild) {
        this.id = id;
        this.discordID = discordID;
        this.guilds = new ArrayList<>();
        this.guilds.add(guild);
    }

    public long getId() {
        return id;
    }

    public String getDiscordID() {
        return discordID;
    }

    public List<CGuild> getGuilds() {
        return guilds;
    }

    public void addGuild(CGuild guild) {
        guilds.add(guild);
    }

    public void removeGuild(CGuild guild) {
        guilds.remove(guild);
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public boolean isDonator() {
        return donator;
    }

    public void setDonator(boolean donator) {
        this.donator = donator;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
}
