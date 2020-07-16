package gg.hound.bungeecore.listeners;

import gg.hound.bungeecore.maintainence.MaintenanceMode;
import gg.hound.bungeecore.motd.MessageOfTheDay;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;

public class ProxyPingListener implements Listener {

    private final MaintenanceMode maintenanceMode;
    private final MessageOfTheDay messageOfTheDay;

    public ProxyPingListener(MaintenanceMode maintenanceMode, MessageOfTheDay messageOfTheDay) {
        this.maintenanceMode = maintenanceMode;
        this.messageOfTheDay = messageOfTheDay;
    }

    private final List<String> maintenanceLines = Arrays.asList(
            "§f⚔ §c§lHound Network §f⚔",
            "",
            "§c§lHound Network §cis currently under maintenance",
            ""
    );


    @EventHandler(priority = 8)
    public void onProxyPing(ProxyPingEvent proxyPingEvent) {
        ServerPing serverPing = proxyPingEvent.getResponse();

        if (maintenanceMode.isMaintenanceMode()) {

            ServerPing.PlayerInfo[] sample = new ServerPing.PlayerInfo[maintenanceLines.size()];

            for (int i = 0; i < sample.length; i++) {
                sample[i] = new ServerPing.PlayerInfo(maintenanceLines.get(i), "");
            }

            serverPing.setVersion(new ServerPing.Protocol("hound-cord", 999));
            serverPing.setPlayers(new ServerPing.Players(0, 0, sample));
            serverPing.setDescriptionComponent(new TextComponent("§c§lHound Network §7- §fCurrently Whitelisted." + "\n" + "§cWe are currently in maintenance."));
            proxyPingEvent.setResponse(serverPing);
            return;
        }

        if (proxyPingEvent.getConnection().getVersion() < 47) {
            serverPing.setVersion(new ServerPing.Protocol("Use 1.8 - 1.15", 999));
        }

        serverPing.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', messageOfTheDay.getLine(1)) + "\n" + ChatColor.translateAlternateColorCodes('&', messageOfTheDay.getLine(2))));

        proxyPingEvent.setResponse(serverPing);

    }
}
