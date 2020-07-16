package gg.hound.bungeecore.servermanager;

import gg.hound.bungeecore.redis.RedisManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;

public class PrivateServerManager {

    private final List<String> types = Arrays.asList("uhc", "dev-server-no-fake-dont-do-it");
    private final RedisManager redisManager;

    public PrivateServerManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void createServer(ProxiedPlayer proxiedPlayer, String type) {
        if (!types.contains(type)) {
            proxiedPlayer.sendMessage(new ComponentBuilder(ChatColor.RED.toString() + ChatColor.BOLD + "This server type does not exist.").create());
            return;
        }

        redisManager.createPrivateServer(proxiedPlayer, type);

    }

    public void shutdownServer(ProxiedPlayer proxiedPlayer, String serverName) {
        if (ProxyServer.getInstance().getServerInfo(serverName) == null) {
            proxiedPlayer.sendMessage(new ComponentBuilder(ChatColor.RED.toString() + ChatColor.BOLD + "This server does not exist.").create());
            return;
        }

        redisManager.shutdownServer(proxiedPlayer, serverName);
    }
}
