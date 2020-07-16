package gg.hound.bungeecore.listeners;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.events.ServerDeleteEvent;
import gg.hound.bungeecore.servermanager.ServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerDeleteListener implements Listener {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final ServerManager serverManager;

    public ServerDeleteListener(BungeeCorePlugin bungeeCorePlugin, ServerManager serverManager) {
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.serverManager = serverManager;
    }

    @EventHandler
    public void onServerDelete(ServerDeleteEvent serverDeleteEvent) {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverDeleteEvent.getServerName());

        if (serverInfo != null) {
            for (ProxiedPlayer proxiedPlayer : serverInfo.getPlayers()) {
                if (serverInfo.getName().toLowerCase().contains("lobby-"))
                    proxiedPlayer.disconnect(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "Server you were on has been deleted.").create());
                else
                    proxiedPlayer.connect(serverManager.getRandomHub(), ServerConnectEvent.Reason.SERVER_DOWN_REDIRECT);
            }
            ProxyServer.getInstance().getServers().remove(serverDeleteEvent.getServerName().toUpperCase());
        }
    }
}
