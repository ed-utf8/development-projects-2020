package gg.hound.bungeecore.motd;

import gg.hound.bungeecore.BungeeCorePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;

public class MessageOfTheDay {

    private final BungeeCorePlugin bungeeCorePlugin;

    private HashMap<Integer, String> integerStringHashMap;

    public MessageOfTheDay(BungeeCorePlugin bungeeCorePlugin) {
        this.bungeeCorePlugin = bungeeCorePlugin;

        this.integerStringHashMap = new HashMap<>();
    }

    public void setLine(int line, String motd) {
        integerStringHashMap.put(line, motd);
        bungeeCorePlugin.getConfig().set("motd.line" + line, motd);
        bungeeCorePlugin.saveConfig();
    }

    public String getLine(int line) {
        return ChatColor.translateAlternateColorCodes('&', integerStringHashMap.getOrDefault(line, "Default Motd Line " + line));
    }

    public void loadMoTD(Configuration configuration) {
        setLine(1, configuration.getString("motd.line1"));
        setLine(2, configuration.getString("motd.line2"));
    }

}
