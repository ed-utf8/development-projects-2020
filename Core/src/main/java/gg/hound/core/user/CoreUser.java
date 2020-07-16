package gg.hound.core.user;

import gg.hound.core.disguise.SQLReDisguiseObject;
import gg.hound.core.disguise.api.DisguiseObject;
import gg.hound.core.group.Group;
import gg.hound.core.group.GroupManager;
import gg.hound.core.util.Colour;
import gg.hound.core.util.Prefix;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CoreUser {

    private final UUID uuid;

    private final GroupManager groupManager;
    private final List<Group> permissionGroups;
    private long userId;
    private String userName;
    private Colour colour;
    private Prefix prefix;
    private String specialPrefix = null;
    private Map<String, Long> ignoredUsers;

    private Group highest;

    private boolean muted = false;
    private long unmuteTime = 1;

    private boolean disguised = false;
    private DisguiseObject undisguiseObject = null;
    private SQLReDisguiseObject sqlReDisguiseObject = null;

    private boolean staffNotifications = true, privateMessages = true;

    private long ipId;

    public CoreUser(UUID uuid, GroupManager groupManager) {
        this.uuid = uuid;
        this.groupManager = groupManager;

        this.permissionGroups = new ArrayList<>();
        this.ignoredUsers = new HashMap<>();
        this.highest = groupManager.getGroup("default");
        addGroup(groupManager.getGroup("default"));
    }

    public List<Group> getPermissionGroups() {
        return permissionGroups;
    }

    public void updatePrefix() {
        this.highest = groupManager.getGroup("default");
        for (Group permissionGroup : permissionGroups) {
            if (permissionGroup.getFormatWeight() >= highest.getFormatWeight())
                this.highest = permissionGroup;
        }
    }

    public void addGroup(Group permissionGroup) {
        this.permissionGroups.add(permissionGroup);
        updatePrefix();
    }

    public void removeGroup(Group permissionGroup) {
        this.permissionGroups.remove(permissionGroup);
        updatePrefix();
    }

    public Group getHighest(PowerType powerType) {
        Group group = groupManager.getGroup("default");

        switch (powerType) {

            case ASSIGN: {
                for (Group permissionGroup : permissionGroups) {
                    if (permissionGroup.getAssignPower() >= group.getAssignPower())
                        group = permissionGroup;
                }
                break;
            }

            case PUNISH: {
                for (Group permissionGroup : permissionGroups) {
                    if (permissionGroup.getPunishPower() >= group.getPunishPower())
                        group = permissionGroup;
                }
                break;
            }

            case PREFIX: {
                for (Group permissionGroup : permissionGroups) {
                    if (permissionGroup.getFormatWeight() >= group.getFormatWeight())
                        group = permissionGroup;
                }
                break;
            }
        }

        return group;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isPrivateMessages() {
        return privateMessages;
    }

    public void setPrivateMessages(boolean privateMessages) {
        this.privateMessages = privateMessages;
    }

    public boolean isStaffNotifications() {
        return staffNotifications;
    }

    public void setStaffNotifications(boolean staffNotifications) {
        this.staffNotifications = staffNotifications;
    }

    public String getPrefix() {

        if (disguised)
            return ChatColor.GRAY.toString();

        if (highest == null)
            return ChatColor.GRAY.toString();

        if (prefix != null)
            return prefix.getPrefix() + highest.getPrefix() + colour.colourize();

        if (colour == null)
            return highest.getPrefix();

        if (permissionGroups.size() > 1)
            return highest.getPrefix() + colour.colourize();

        return highest.getPrefix();
    }

    public boolean isDisguised() {
        return disguised;
    }

    public void setDisguised(DisguiseObject disguise) {
        this.undisguiseObject = disguise;
        this.disguised = true;
    }

    public DisguiseObject getUndisguiseObject() {
        return undisguiseObject;
    }

    public void setUnDisguised() {
        this.disguised = false;
    }

    public SQLReDisguiseObject getSqlReDisguiseObject() {
        return sqlReDisguiseObject;
    }

    public void setSqlReDisguiseObject(SQLReDisguiseObject sqlReDisguiseObject) {
        this.sqlReDisguiseObject = sqlReDisguiseObject;
    }

    public void setSpecialPrefix(String specialPrefix) {
        this.specialPrefix = specialPrefix;
    }

    public long getUnMuteTime() {
        return unmuteTime;
    }

    public void setUnMuteTime(long unmuteTime) {
        this.unmuteTime = unmuteTime;
    }

    public Map<String, Long> getIgnoredUsers() {
        return ignoredUsers;
    }

    public void setIgnoredUsers(Map<String, Long> ignoredUsers) {
        this.ignoredUsers = ignoredUsers;
    }

    public long getIpId() {
        return ipId;
    }

    public void setIpId(long ipId) {
        this.ipId = ipId;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public boolean hasRegistered() {
        return false;
    }

    public TempInfoStoreUser getTempUser() {
        return new TempInfoStoreUser(userId, uuid, userName, ipId);
    }

    public enum PowerType {

        ASSIGN,
        PUNISH,
        PREFIX
    }
}
