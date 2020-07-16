package gg.hound.core.group;

import gg.hound.core.util.Colour;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class GroupManager {

    private final HashMap<String, Group> permissionGroupHashMap;

    public GroupManager() {

        Map<String, Boolean> defaultPermissions = new HashMap<>();

        defaultPermissions.put("bukkit.command.me", false);
        defaultPermissions.put("bukkit.command.about", false);
        defaultPermissions.put("bukkit.command.kill", false);
        defaultPermissions.put("bukkit.command.achievement", false);
        defaultPermissions.put("bukkit.command.help", false);
        defaultPermissions.put("minecraft.command.kill", false);
        defaultPermissions.put("minecraft.command.me", false);
        defaultPermissions.put("minecraft.command.version", false);
        defaultPermissions.put("bukkit.*", false);
        defaultPermissions.put("minecraft.*", false);
        defaultPermissions.put("worldedit.calculate", false);
        defaultPermissions.put("worldedit.calc", false);

        this.permissionGroupHashMap = new HashMap<>();

        this.permissionGroupHashMap.put("default", new Group(-1, defaultPermissions, "default", "§7", "§8\u00bb", -1, 0, 0, 0, 0, new Colour(-1, "REEEEETWHITE", ChatColor.WHITE, false, false, false, false), false, false));
    }

    public HashMap<String, Group> getPermissionGroupHashMap() {
        return permissionGroupHashMap;
    }

    public boolean groupExists(String groupName) {
        return !permissionGroupHashMap.containsKey(groupName.toLowerCase());
    }

    public Group getGroup(String groupName) {
        if (groupExists(groupName.toLowerCase())) {
            return permissionGroupHashMap.get("default");
        }
        return permissionGroupHashMap.get(groupName.toLowerCase());
    }


    public void createGroup(String name, Group group) {
        this.permissionGroupHashMap.put(name, group);
    }
}
