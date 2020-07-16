package gg.hound.core.listeners;

import gg.hound.core.CorePlugin;
import gg.hound.core.group.Group;
import gg.hound.core.group.GroupManager;
import gg.hound.core.punishments.IPBan;
import gg.hound.core.punishments.Punishment;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.punishments.Reason;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.tasks.PlayerCooldownTask;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import gg.hound.core.util.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerChatListener implements Listener {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final PlayerCooldownTask playerCooldownTask;
    private final SQLManager sqlManager;
    private final RedisManager redisManager;
    private final PluginUtils pluginUtils;
    private final PunishmentData punishmentData;
    private final GroupManager groupManager;

    public PlayerChatListener(CorePlugin corePlugin, UserManager userManager, PlayerCooldownTask playerCooldownTask, SQLManager sqlManager, RedisManager redisManager, PluginUtils pluginUtils, PunishmentData punishmentData, GroupManager groupManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.playerCooldownTask = playerCooldownTask;

        this.sqlManager = sqlManager;
        this.redisManager = redisManager;
        this.pluginUtils = pluginUtils;
        this.punishmentData = punishmentData;
        this.groupManager = groupManager;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {

        Player player = asyncPlayerChatEvent.getPlayer();
        CoreUser coreUser = userManager.getUser(player.getUniqueId());

        if (playerCooldownTask.hasChatCooldown(coreUser)) {
            player.sendMessage(corePlugin.getPrefix() + "Please wait before sending another message...");
            asyncPlayerChatEvent.setCancelled(true);
            return;
        }

        if (coreUser.isMuted()) {
            if (coreUser.getUnMuteTime() > pluginUtils.currentTime() && coreUser.getUnMuteTime() != 0) {
                asyncPlayerChatEvent.setCancelled(true);
                player.sendMessage(corePlugin.getPrefix() + "You are currently muted until " + pluginUtils.getDate(coreUser.getUnMuteTime()));
                return;
            } else
                coreUser.setMuted(false);
        }

        if (punishmentData.isPlayerAutomuted(player.getUniqueId())) {
            asyncPlayerChatEvent.setCancelled(true);
            player.sendMessage(corePlugin.getPrefix() + "You are currently auto-muted!");
            return;
        }

        if (punishmentData.containsWord(asyncPlayerChatEvent.getMessage())) {
            asyncPlayerChatEvent.setCancelled(true);
            player.sendMessage(corePlugin.getPrefix() + "You have said an auto-muted word and can now no longer speak for 5 minutes.");

            punishmentData.addAutomutedPlayer(player.getUniqueId());
            return;
        }

        if (!(asyncPlayerChatEvent.getPlayer().hasPermission("core.staff") || asyncPlayerChatEvent.getPlayer().hasPermission("chat.bypass") || asyncPlayerChatEvent.getPlayer().hasPermission("core.famous")))
            playerCooldownTask.addChatCooldown(coreUser);

        asyncPlayerChatEvent.setMessage(asyncPlayerChatEvent.getMessage().replaceAll("%", "%%"));

        asyncPlayerChatEvent.setMessage(asyncPlayerChatEvent.getMessage().replaceAll("<3", "§c❤§f"));

        if (!coreUser.isDisguised()) {
            Group group = coreUser.getHighest(CoreUser.PowerType.PREFIX);
            asyncPlayerChatEvent.setFormat(coreUser.getPrefix() + asyncPlayerChatEvent.getPlayer().getName() + group.getChatSeparator() + group.getChatColour().colourize() + asyncPlayerChatEvent.getMessage());
        } else {
            Group group = groupManager.getGroup("default");
            asyncPlayerChatEvent.setFormat(group.getPrefix() + asyncPlayerChatEvent.getPlayer().getName() + group.getChatSeparator() + group.getChatColour().colourize() + asyncPlayerChatEvent.getMessage());
        }


        if (corePlugin.isChatMuted()) {
            if (!asyncPlayerChatEvent.getPlayer().hasPermission("core.staff")) {
                asyncPlayerChatEvent.setCancelled(true);
                asyncPlayerChatEvent.getPlayer().sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Global chat is disabled.");
                return;
            }

            if (coreUser.isDisguised()) {
                asyncPlayerChatEvent.setCancelled(true);
                asyncPlayerChatEvent.getPlayer().sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Global chat is muted, you would normally bypass but as you are disguised we are blocking this!");
            }
            return;
        }

        /*

        IP BAN

         */

        if (punishmentData.getOtherIPBan().contains(player.getUniqueId())) {
            asyncPlayerChatEvent.setCancelled(true);
            if (asyncPlayerChatEvent.getMessage().equalsIgnoreCase("cancel")) {
                punishmentData.removeOtherIPBan(player.getUniqueId());
                punishmentData.removeIPBan(player.getUniqueId());
                player.sendMessage(corePlugin.getPrefix() + "Punishment has been cancelled.");
                return;
            }

            Punishment ipBan = punishmentData.getIPBan(player.getUniqueId());
            Reason reason = ipBan.getReason();

            reason.setCustomReason(asyncPlayerChatEvent.getMessage());

            if (sqlManager.ipbanPlayer(ipBan))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact a developer!");
            else {
                Bukkit.getServer().getScheduler().runTask(corePlugin, () -> {
                    Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + ipBan.getTarget().getName() + ChatColor.RED + " has been banned for " + ChatColor.YELLOW + asyncPlayerChatEvent.getMessage());
                    if (Bukkit.getPlayer(ipBan.getTarget().getUuid()) != null) {
                        Bukkit.getPlayer(ipBan.getTarget().getUuid()).kickPlayer(ChatColor.RED + "You have been banned for: " + ChatColor.WHITE + asyncPlayerChatEvent.getMessage());
                    } else
                        redisManager.sendBan(ipBan.getTarget().getUuid(), asyncPlayerChatEvent.getMessage());
                });
            }
            punishmentData.removeOtherIPBan(player.getUniqueId());
            punishmentData.removeIPBan(player.getUniqueId());
        }


        /*

        BAN

         */

        if (punishmentData.getOtherBan().contains(player.getUniqueId())) {
            asyncPlayerChatEvent.setCancelled(true);
            if (asyncPlayerChatEvent.getMessage().equalsIgnoreCase("cancel")) {
                punishmentData.removeOtherBan(player.getUniqueId());
                punishmentData.removeBan(player.getUniqueId());
                player.sendMessage(corePlugin.getPrefix() + "Punishment has been cancelled.");
                return;
            }

            Punishment ban = punishmentData.getBan(player.getUniqueId());
            Reason reason = ban.getReason();

            reason.setCustomReason(asyncPlayerChatEvent.getMessage());

            if (sqlManager.banPlayer(ban))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact a developer!");
            else {
                Bukkit.getServer().getScheduler().runTask(corePlugin, () -> {
                    Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + ban.getTarget().getName() + ChatColor.RED + " has been banned for " + ChatColor.YELLOW + asyncPlayerChatEvent.getMessage());
                    if (Bukkit.getPlayer(ban.getTarget().getUuid()) != null) {
                        Bukkit.getPlayer(ban.getTarget().getUuid()).kickPlayer(ChatColor.RED + "You have been banned for: " + ChatColor.WHITE + asyncPlayerChatEvent.getMessage());
                    } else
                        redisManager.sendBan(ban.getTarget().getUuid(), asyncPlayerChatEvent.getMessage());
                });
            }
            punishmentData.removeOtherBan(player.getUniqueId());
            punishmentData.removeBan(player.getUniqueId());
        }

        /*

        MUTE

         */

        if (punishmentData.getOtherMute().contains(player.getUniqueId())) {
            asyncPlayerChatEvent.setCancelled(true);
            if (asyncPlayerChatEvent.getMessage().equalsIgnoreCase("cancel")) {
                punishmentData.removeOtherMute(player.getUniqueId());
                punishmentData.removeMute(player.getUniqueId());
                player.sendMessage(corePlugin.getPrefix() + "Punishment has been cancelled.");
                return;
            }
            Punishment mute = punishmentData.getMute(player.getUniqueId());
            Reason reason = mute.getReason();

            reason.setCustomReason(asyncPlayerChatEvent.getMessage());

            if (sqlManager.mutePlayer(mute))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact a developer!");
            else {
                Bukkit.getServer().getScheduler().runTask(corePlugin, () -> {
                    Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + mute.getTarget().getName() + " has been muted for " + ChatColor.YELLOW + asyncPlayerChatEvent.getMessage());
                    if (Bukkit.getPlayer(mute.getTarget().getUuid()) != null) {
                        CoreUser muteTarget = userManager.getUser(mute.getTarget().getUuid());
                        if (muteTarget != null) {
                            muteTarget.setMuted(true);
                            muteTarget.setUnMuteTime(pluginUtils.getUnmuteTime(mute.getTime()));
                        }
                    } else
                        redisManager.sendMute(mute.getTarget().getUuid(), asyncPlayerChatEvent.getMessage(), pluginUtils.getUnmuteTime(mute.getTime()));
                });
            }
            punishmentData.removeOtherMute(player.getUniqueId());
            punishmentData.removeMute(player.getUniqueId());
        }

        /*

        KICK

         */

        if (punishmentData.getOtherKick().contains(player.getUniqueId())) {
            asyncPlayerChatEvent.setCancelled(true);
            if (asyncPlayerChatEvent.getMessage().equalsIgnoreCase("cancel")) {
                punishmentData.removeOtherKick(player.getUniqueId());
                punishmentData.removeKick(player.getUniqueId());
                player.sendMessage(corePlugin.getPrefix() + "Punishment has been cancelled.");
                return;
            }
            Punishment kick = punishmentData.getKick(player.getUniqueId());
            Reason reason = kick.getReason();

            reason.setCustomReason(asyncPlayerChatEvent.getMessage());

            if (sqlManager.kickPlayer(kick))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact a developer!");
            else {
                Bukkit.getServer().getScheduler().runTask(corePlugin, () -> {
                    Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + kick.getTarget().getName() + ChatColor.RED + " has been kicked for " + ChatColor.YELLOW + asyncPlayerChatEvent.getMessage());
                    if (Bukkit.getPlayer(kick.getTarget().getUuid()) != null) {
                        Bukkit.getPlayer(kick.getTarget().getUuid()).kickPlayer(ChatColor.RED + "You have been kicked for: " + ChatColor.WHITE + asyncPlayerChatEvent.getMessage());
                    }
                });
            }
            punishmentData.removeOtherKick(player.getUniqueId());
            punishmentData.removeKick(player.getUniqueId());
        }

        /*

        REPORT

         */

        if (punishmentData.getOtherReport().contains(player.getUniqueId())) {
            asyncPlayerChatEvent.setCancelled(true);
            if (asyncPlayerChatEvent.getMessage().equalsIgnoreCase("cancel")) {
                punishmentData.removeOtherReport(player.getUniqueId());
                player.sendMessage(corePlugin.getPrefix() + "Punishment has been cancelled.");
                return;
            }
            Report report = punishmentData.getReport(player.getUniqueId());
            report.setReason(asyncPlayerChatEvent.getMessage());

            redisManager.sendReport(report.getReporter(), report.getTarget(), asyncPlayerChatEvent.getMessage());

            punishmentData.removeOtherReport(player.getUniqueId());
        }

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        Player player = playerCommandPreprocessEvent.getPlayer();
        CoreUser coreUser = userManager.getUser(player.getUniqueId());

        if (coreUser.isMuted()) {
            if (playerCommandPreprocessEvent.getMessage().toLowerCase().startsWith("/helpop")) {
                if (coreUser.getUnMuteTime() > pluginUtils.currentTime() && coreUser.getUnMuteTime() != 0) {
                    playerCommandPreprocessEvent.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You are currently muted.");
                } else
                    coreUser.setMuted(false);
            }
        }

        if (playerCommandPreprocessEvent.getMessage().toLowerCase().startsWith("/mobai")) {
            if (!player.isOp()) {
                playerCommandPreprocessEvent.setCancelled(true);
                player.sendMessage("§cIncorrect Permissions!");
                return;
            }
        }
    }
}
