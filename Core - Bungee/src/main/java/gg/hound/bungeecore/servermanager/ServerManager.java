package gg.hound.bungeecore.servermanager;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.redis.RedisManager;
import gg.hound.bungeecore.sql.SQLManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final RedisManager redisManager;
    private final SQLManager sqlManager;

    public ServerManager(BungeeCorePlugin bungeeCorePlugin, RedisManager redisManager, SQLManager sqlManager) {
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.redisManager = redisManager;
        this.sqlManager = sqlManager;
    }

    public void createServer(CommandSender commandSender, String serverName, String serverType, String serverAddress, int serverPort, boolean serverPrivate) {
        if (ProxyServer.getInstance().getServerInfo(serverName) != null) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§cServer already exists.").create());
            return;
        }

        redisManager.createServer(serverName, serverType, serverAddress, serverPort);
        sqlManager.createServer(serverName, serverType, serverAddress, serverPort, serverPrivate);
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§cServer create request has been sent to service manager.").create());
    }

    public void deleteServer(CommandSender commandSender, String serverName) {
        if (ProxyServer.getInstance().getServerInfo(serverName) == null) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§cServer does not exist.").create());
            return;
        }
        redisManager.deleteServer(serverName);
        sqlManager.deleteServer(serverName);
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§cServer has been deleted.").create());
    }

    public void listGameInstances(CommandSender commandSender) {
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§eServers running on Bungee(Network) Instance§7:").create());
        for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + info.getName()).create());
        }
    }

    public ServerInfo getRandomHub(List<String> excludeServers) {
        List<ServerInfo> hubServers = new ArrayList<>();

        for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if (serverInfo.getName().toLowerCase().startsWith("lobby-")) {
                if (!excludeServers.contains(serverInfo.getName().toLowerCase()))
                    hubServers.add(serverInfo);
            }
        }

        ServerInfo hubServer;
        if (hubServers.size() == 0) {
            hubServer = ProxyServer.getInstance().getServers().values().iterator().next();
        } else {
            hubServer = hubServers.get(0);
        }

        for (ServerInfo serverInfo : hubServers) {
            if (hubServer.getPlayers().size() > serverInfo.getPlayers().size()) {
                hubServer = serverInfo;
            }
        }

        return hubServer;
    }

    public ServerInfo getRandomHub() {
        List<ServerInfo> hubServers = new ArrayList<>();

        for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
            if (serverInfo.getName().toLowerCase().startsWith("lobby-")) {
                hubServers.add(serverInfo);
            }
        }

        ServerInfo hubServer;
        if (hubServers.size() == 0) {
            hubServer = ProxyServer.getInstance().getServers().values().iterator().next();
        } else {
            hubServer = hubServers.get(0);
        }

        for (ServerInfo serverInfo : hubServers) {
            if (hubServer.getPlayers().size() > serverInfo.getPlayers().size()) {
                hubServer = serverInfo;
            }
        }

        return hubServer;
    }


}
