package gg.hound.core.group;

import gg.hound.core.util.Colour;
import org.bukkit.ChatColor;

import java.util.Map;

public class Group {

    private final int rankId;
    private final int formatWeight;
    private final int punishPower;
    private final int neededPunishPower;
    private final int assignPower;
    private final int neededAssignPower;

    private final String rankName;
    private final String rankPrefix;
    private final String chatSeparator;
    private final Colour chatColour;
    private final boolean staff;
    private final boolean admin;

    private final Map<String, Boolean> stringBooleanHashMap;

    public Group(int rankId, Map<String, Boolean> stringBooleanHashMap, String rankName, String rankPrefix, String chatSeparator, int formatWeight, int punishPower, int neededPunishPower, int assignPower, int neededAssignPower, Colour chatColour, boolean staff, boolean admin) {
        this.rankId = rankId;
        this.stringBooleanHashMap = stringBooleanHashMap;
        this.rankName = rankName;
        this.rankPrefix = rankPrefix.replace("(star)", "✦");
        this.chatSeparator = " " + ChatColor.translateAlternateColorCodes('&', chatSeparator) + " ";
        this.formatWeight = formatWeight;
        this.punishPower = punishPower;
        this.neededPunishPower = neededPunishPower;
        this.assignPower = assignPower;
        this.neededAssignPower = neededAssignPower;
        this.chatColour = chatColour;
        this.staff = staff;
        this.admin = admin;

        if (staff)
            stringBooleanHashMap.put("core.staff", true);

        if (admin)
            stringBooleanHashMap.put("core.admin", true);
    }

    public int getRankId() {
        return rankId;
    }

    public int getFormatWeight() {
        return formatWeight;
    }

    public int getPunishPower() {
        return punishPower;
    }

    public int getNeededPunishPower() {
        return neededPunishPower;
    }

    public String getRankName() {
        return rankName;
    }

    public int getAssignPower() {
        return assignPower;
    }

    public int getNeededAssignPower() {
        return neededAssignPower;
    }

    public boolean isStaff() {
        return staff;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Colour getChatColour() {
        return chatColour;
    }

    public String getChatSeparator() {
        return chatSeparator;
    }

    public Map<String, Boolean> getStringBooleanHashMap() {
        return stringBooleanHashMap;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', rankPrefix);
    }


}
