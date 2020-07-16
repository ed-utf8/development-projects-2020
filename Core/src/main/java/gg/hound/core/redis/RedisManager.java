package gg.hound.core.redis;

import gg.hound.core.CorePlugin;
import gg.hound.core.events.AdminChatEvent;
import gg.hound.core.events.CoreUserRankUpdateEvent;
import gg.hound.core.events.MuteEvent;
import gg.hound.core.events.ReportUserEvent;
import gg.hound.core.events.StaffChatEvent;
import gg.hound.core.events.UnmuteEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisManager {

    private final String SPLIT = "#~#";
    private final String announcementPrefix = "§6§lAnnouncement §8\u00bb §e";

    private final JedisPool jedisPool;

    private final CorePlugin corePlugin;

    public RedisManager(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxTotal(20);
        jedisPool = new JedisPool(jedisPoolConfig, corePlugin.getConfig().getString("redis.host"), corePlugin.getConfig().getInt("redis.port"), 2000, corePlugin.getConfig().getString("redis.pass"));
    }

    public void onDisable() {
        jedisPool.close();
    }

    public void onEnable() {
        jedisPool.getResource().connect();

        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.ping().equalsIgnoreCase("PONG")) {
                corePlugin.log("Redis server has been initialized.");
            }
        }

        JedisPubSub jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                String[] arguments = message.split(SPLIT);
                switch (channel.toLowerCase()) {

                    case "mc_announcement":
                        if (arguments[0].equalsIgnoreCase("uhc")) {
                            TextComponent broadcast = new TextComponent("§6§l[UHC] §eThe Whitelist for §6" + arguments[1] + "§e is now off!");
                            broadcast.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + arguments[1]));
                            broadcast.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join server").create()));
                            Bukkit.getServer().spigot().broadcast(broadcast);
                        } else if (arguments[0].equalsIgnoreCase("private-uhc")) {
                            TextComponent broadcast = new TextComponent("§6§l[PRIVATE-UHC] §eThe Whitelist for §6" + arguments[1] + "§e is now off!");
                            broadcast.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + arguments[1]));
                            broadcast.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join server").create()));
                            Bukkit.getServer().spigot().broadcast(broadcast);
                        } else if (arguments[0].equalsIgnoreCase("auto-uhc")) {
                            TextComponent broadcast = new TextComponent("§6§l[AUTO-UHC] §eThe Whitelist for §6" + arguments[1] + "§e is now off!");
                            broadcast.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + arguments[1]));
                            broadcast.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join server").create()));
                            Bukkit.getServer().spigot().broadcast(broadcast);
                        } else {
                            Bukkit.getServer().broadcastMessage(announcementPrefix + ChatColor.translateAlternateColorCodes('&', arguments[1]));
                        }
                        break;

                    case "mc_groups":
                        Bukkit.getServer().getPluginManager().callEvent(new CoreUserRankUpdateEvent(CoreUserRankUpdateEvent.GroupAction.valueOf(arguments[0].toUpperCase()), UUID.fromString(arguments[1]), arguments[2]));
                        break;

                    case "backend_reboot":
                        Bukkit.getServer().shutdown();
                        break;

                    case "mc_chat":
                        Bukkit.getServer().getPluginManager().callEvent(new StaffChatEvent(arguments[0], arguments[1], arguments[2]));
                        break;

                    case "mc_adminchat":
                        Bukkit.getServer().getPluginManager().callEvent(new AdminChatEvent(arguments[0], arguments[1], arguments[2]));
                        break;

                    case "mc_report":
                        Bukkit.getServer().getPluginManager().callEvent(new ReportUserEvent(arguments[0], arguments[1], arguments[2], arguments[3]));
                        break;

                    case "mc_ban":
                        if (Bukkit.getPlayer(UUID.fromString(arguments[0])) != null) {
                            Bukkit.getPlayer(UUID.fromString(arguments[0])).kickPlayer(ChatColor.RED + "You have been banned for\n" + arguments[1]);
                        }
                        break;

                    case "mc_mute":
                        if (Bukkit.getPlayer(UUID.fromString(arguments[0])) != null) {
                            Bukkit.getServer().getPluginManager().callEvent(new MuteEvent(UUID.fromString(arguments[0]), arguments[1], Long.parseLong(arguments[2])));
                        }
                        break;

                    case "mc_unmute":
                        if (Bukkit.getPlayer(UUID.fromString(arguments[0])) != null) {
                            Bukkit.getServer().getPluginManager().callEvent(new UnmuteEvent(UUID.fromString(arguments[0]), Long.parseLong(arguments[1])));
                        }
                }
            }
        };

        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(jedisPubSub, "mc_groups", "mc_chat", "mc_report", "mc_adminchat", "backend_reboot", "mc_ban", "mc_mute", "mc_unmute", "mc_announcement");
                corePlugin.log("Subscription ended.");
            } catch (Exception exception) {
                corePlugin.log("Subscribing failed." + exception.getMessage());
            }
        }).start();
    }

    public void sendRankUpdate(CoreUserRankUpdateEvent.GroupAction groupAction, UUID uuid, String group) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_groups", groupAction.name().toLowerCase() + SPLIT + uuid.toString() + SPLIT + group);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendStaffChat(String userName, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_chat", userName + SPLIT + corePlugin.getServerName() + SPLIT + message);
        }
    }

    public void sendAdminChat(String userName, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_adminchat", userName + SPLIT + corePlugin.getServerName() + SPLIT + message);
        }
    }

    public void sendReport(String userName, String userReported, String userReportedReason) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_report", userName + SPLIT + corePlugin.getServerName() + SPLIT + userReported + SPLIT + userReportedReason);
        }
    }

    public void sendAnnouncement(String announcement) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_announcement", "global" + SPLIT + announcement);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendUHC(String announcement) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_announcement", "uhc" + SPLIT + announcement);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendReboot() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("backend_reboot", "global" + SPLIT + "reboot");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendBan(UUID target, String reason) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_ban", target.toString() + SPLIT + reason);
        }
    }

    public void sendMute(UUID target, String reason, long time) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_mute", target.toString() + SPLIT + reason + SPLIT + time);
        }
    }

    public void sendUnmute(UUID target, long time) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish("mc_unmute", target.toString() + SPLIT + time);
        }
    }

}
