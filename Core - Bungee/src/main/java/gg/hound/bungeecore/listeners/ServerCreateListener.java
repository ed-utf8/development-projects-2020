package gg.hound.bungeecore.listeners;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.events.ServerCreateEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class ServerCreateListener implements Listener {

    private final BungeeCorePlugin bungeeCorePlugin;

    public ServerCreateListener(BungeeCorePlugin bungeeCorePlugin) {
        this.bungeeCorePlugin = bungeeCorePlugin;
    }

    @EventHandler
    public void onServerCreate(ServerCreateEvent serverCreateEvent) {
        ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(serverCreateEvent.getServerName().toUpperCase(), new InetSocketAddress(serverCreateEvent.getServerAddress(), serverCreateEvent.getServerPort()), "", false);
        ProxyServer.getInstance().getServers().put(serverCreateEvent.getServerName().toUpperCase(), serverInfo);
    }
}
