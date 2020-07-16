package gg.hound.bungeecore.redis;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.events.ServerCreateEvent;
import gg.hound.bungeecore.events.ServerDeleteEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class RedisManager {

    private String SPLIT = "#~#";

    private JedisPool jedisPool;

    private final BungeeCorePlugin bungeeCorePlugin;

    public RedisManager(BungeeCorePlugin bungeeCorePlugin) {
        this.bungeeCorePlugin = bungeeCorePlugin;

    }

    public void onDisable() {
        jedisPool.close();
    }

    public void onEnable() {

        FutureTask<JedisPool> task = new FutureTask<>(() -> {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMinIdle(2);
            jedisPoolConfig.setMaxTotal(8);
            return new JedisPool(jedisPoolConfig, bungeeCorePlugin.getConfig().getString("redis-host"), bungeeCorePlugin.getConfig().getInt("redis-port"), 2000, bungeeCorePlugin.getConfig().getString("redis-pass"));
        });

        ProxyServer.getInstance().getScheduler().runAsync(bungeeCorePlugin, task);

        try {
            jedisPool = task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ProxyServer.getInstance().getScheduler().runAsync(bungeeCorePlugin, () -> jedisPool.getResource().connect());

        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.ping().equalsIgnoreCase("PONG")) {
                bungeeCorePlugin.log("Redis server has been initialized.");
            }
        }

        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                String[] arguments = message.split(SPLIT);
                switch (channel.toLowerCase()) {
                    case "mc_game_instance":
                        switch (arguments[0]) {
                            case "create":
                                ProxyServer.getInstance().getPluginManager().callEvent(new ServerCreateEvent(arguments[1], arguments[3], Integer.parseInt(arguments[4])));
                                break;

                            case "delete":
                                ProxyServer.getInstance().getPluginManager().callEvent(new ServerDeleteEvent(arguments[1]));
                                break;
                        }
                        break;

                    case "mc_private_servers":
                        switch (arguments[0]) {
                            case "send-to-server": {
                                //sendToServer#~#uuid#~#serverName
                                if (arguments[2].length() == 36)
                                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(arguments[2])) != null)
                                        ProxyServer.getInstance().getPlayer(UUID.fromString(arguments[2])).connect(ProxyServer.getInstance().getServerInfo(arguments[1]));
                                break;
                            }

                            case "sender-error": {
                                if (arguments[1].length() == 36)
                                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(arguments[1])) != null)
                                        ProxyServer.getInstance().getPlayer(UUID.fromString(arguments[1])).sendMessage(new ComponentBuilder(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Error: " + ChatColor.RED.toString() + ChatColor.BOLD + arguments[2]).create());
                                break;
                            }

                            case "sender-success": {
                                if (arguments[1].length() == 36)
                                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(arguments[1])) != null)
                                        ProxyServer.getInstance().getPlayer(UUID.fromString(arguments[1])).sendMessage(new ComponentBuilder(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Success: " + ChatColor.GREEN.toString() + ChatColor.BOLD + arguments[2]).create());
                                break;
                            }
                        }
                        break;

                    case "mc_joinme":
                        TextComponent joinMe = new TextComponent("§7[§6§l" + arguments[0] + "§7] §eEveryone! I'm playing on §6" + arguments[1] + "! §eClick §ehere §eto §eplay §ewith §eme!");
                        joinMe.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + arguments[1]));
                        joinMe.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to join me!").create()));
                        bungeeCorePlugin.getProxy().broadcast(joinMe);
                        break;
                }
            }
        };

        bungeeCorePlugin.getExecutorService().submit(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(jedisPubSub, "mc_announcement", "mc_game_instance", "mc_joinme", "mc_private_servers");
                bungeeCorePlugin.log("Subscription ended.");
            } catch (Exception exception) {
                bungeeCorePlugin.log("Subscribing failed." + exception.getMessage());
            }
        });
    }

    public void createServer(String serverName, String serverType, String serverAddress, int serverPort) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_game_instance", "create" + SPLIT + serverName + SPLIT + serverType + SPLIT + serverAddress + SPLIT + serverPort);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void deleteServer(String serverName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_game_instance", "delete" + SPLIT + serverName);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendStaffJoin(String userName, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_chat", userName + SPLIT + "NETWORK" + SPLIT + message);
        }
    }

    public void sendJoinMe(String player, String server) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_joinme", player + SPLIT + server);
        }
    }

    public boolean getPing() {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.ping().equalsIgnoreCase("PONG")) {
                return true;
            }
        }
        return false;
    }

    public void createPrivateServer(ProxiedPlayer proxiedPlayer, String type) {
        // create-server#~#serverType#~#uuid#~#name
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_private_servers", "create-server" + SPLIT + type + SPLIT + proxiedPlayer.getUniqueId().toString() + SPLIT + proxiedPlayer.getName());
            proxiedPlayer.sendMessage(new ComponentBuilder(ChatColor.GREEN.toString() + ChatColor.BOLD + "Attempting to create your server. Please wait...").create());
        }
    }

    public void shutdownServer(ProxiedPlayer proxiedPlayer, String serverName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_private_servers", "shutdown-server" + SPLIT + serverName + SPLIT + proxiedPlayer.getUniqueId().toString() + SPLIT + proxiedPlayer.getName());
            proxiedPlayer.sendMessage(new ComponentBuilder(ChatColor.GREEN.toString() + ChatColor.BOLD + "Attempting to shutdown server instance. Please wait...").create());
        }
    }
}
