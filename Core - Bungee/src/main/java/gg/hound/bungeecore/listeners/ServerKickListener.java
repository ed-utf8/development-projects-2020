package gg.hound.bungeecore.listeners;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.servermanager.ServerManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final ServerManager serverManager;

    public ServerKickListener(BungeeCorePlugin bungeeCorePlugin, ServerManager serverManager) {
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.serverManager = serverManager;
    }

    @EventHandler
    public void onServerKick(ServerKickEvent serverKickEvent) {
        serverKickEvent.getPlayer().resetTabHeader();
        if (!serverKickEvent.getKickedFrom().getName().toLowerCase().startsWith("lobby-")) {
            serverKickEvent.setCancelled(true);
            serverKickEvent.getPlayer().sendMessage(new ComponentBuilder("§cYou were kicked from the server: §f" + serverKickEvent.getKickReason()).create());
            serverKickEvent.setCancelServer(serverManager.getRandomHub());
        } else {
            serverKickEvent.getPlayer().disconnect(serverKickEvent.getKickReasonComponent());
        }
    }
}
