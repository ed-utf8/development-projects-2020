package gg.hound.core.listeners;

import gg.hound.core.CorePlugin;
import gg.hound.core.disguise.DisguiseManager;
import gg.hound.core.events.MuteEvent;
import gg.hound.core.events.UnmuteEvent;
import gg.hound.core.group.PermissionsHandler;
import gg.hound.core.punishments.Ban;
import gg.hound.core.punishments.IPBan;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final SQLManager sqlManager;
    private final DisguiseManager disguiseManager;
    private final PermissionsHandler permissionsHandler;
    private final PluginUtils pluginUtils;

    public PlayerJoinListener(CorePlugin corePlugin, UserManager userManager, SQLManager sqlManager, DisguiseManager disguiseManager, PermissionsHandler permissionsHandler, PluginUtils pluginUtils) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.sqlManager = sqlManager;
        this.disguiseManager = disguiseManager;
        this.permissionsHandler = permissionsHandler;
        this.pluginUtils = pluginUtils;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent playerPreLoginEvent) {
        CoreUser coreUser = userManager.getUser(playerPreLoginEvent.getUniqueId());

        coreUser.setUserName(playerPreLoginEvent.getName());

        sqlManager.loadUser(coreUser, playerPreLoginEvent.getAddress().getHostAddress());

        if (coreUser.getUserId() <= 1) {
            playerPreLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            playerPreLoginEvent.setKickMessage(ChatColor.DARK_RED + "Internal Error: " + ChatColor.WHITE + "USER_LOAD_ERROR_2");
            return;
        }

        sqlManager.loadUserExtra(coreUser);

        sqlManager.loadUserGroups(coreUser);

        sqlManager.loadDisguise(coreUser);

        sqlManager.loadMute(coreUser);

        coreUser.setIgnoredUsers(sqlManager.getIgnoredUsers(coreUser.getUserId()));

        long time = 5;
        long fiveYears = time * 1000 * 60 * 60 * 24 * 365;
        long permaTime = pluginUtils.currentTime() + fiveYears;

        IPBan ipBan = sqlManager.getIPBan(coreUser);

        if (ipBan != null) {
            if (!corePlugin.getServerName().toLowerCase().startsWith("lobby-")) {
                playerPreLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                if (ipBan.getTime() == -1)
                    playerPreLoginEvent.setKickMessage(ChatColor.RED + "You are currently ip-banned for " + ipBan.getReason().getDisplayName() + "\nYour ban is permanent.");
                else
                    playerPreLoginEvent.setKickMessage(ChatColor.RED + "You are temporarily ip-banned for " + ipBan.getReason().getDisplayName());
            } else {
                coreUser.setMuted(true);
                coreUser.setUnMuteTime(permaTime);
            }
            return;
        }

        Ban ban = sqlManager.getBan(coreUser);

        if (ban != null) {
            if (!corePlugin.getServerName().toLowerCase().startsWith("lobby-")) {
                playerPreLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                if (ban.getTime() == -1)
                    playerPreLoginEvent.setKickMessage(ChatColor.RED + "You are currently banned for " + ban.getReason().getDisplayName() + "\nYour ban is permanent.");
                else
                    playerPreLoginEvent.setKickMessage(ChatColor.RED + "You are currently banned for " + ban.getReason().getDisplayName() + "\nUntil: " + pluginUtils.getDate(ban.getTime()));
            } else {
                coreUser.setMuted(true);
                coreUser.setUnMuteTime(permaTime);
            }
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
        CoreUser coreUser = userManager.getUser(playerLoginEvent.getPlayer().getUniqueId());
        permissionsHandler.addPermissions(playerLoginEvent.getPlayer(), coreUser);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.setJoinMessage(null);

        CoreUser coreUser = userManager.getUser(playerJoinEvent.getPlayer().getUniqueId());

        coreUser.updatePrefix();

        if (coreUser.getSqlReDisguiseObject() != null) {
            disguiseManager.disguisePlayer(playerJoinEvent.getPlayer(), coreUser.getSqlReDisguiseObject().getDisguiseName(), coreUser.getSqlReDisguiseObject().getSkinId());
            //Bukkit.getServer().getPluginManager().callEvent(new DisguisePlayerJoinEvent(coreUser.getUserName(), coreUser.getSqlReDisguiseObject().getDisguiseName()));
        }
    }

    @EventHandler
    public void onMute(MuteEvent muteEvent) {
        CoreUser coreUser = userManager.getUser(Bukkit.getPlayer(muteEvent.getTarget()).getUniqueId());

        if (coreUser != null) {
            Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.RED + muteEvent.getTarget() + " has been muted for " + ChatColor.YELLOW + muteEvent.getReason());
            coreUser.setMuted(true);
            coreUser.setUnMuteTime(muteEvent.getUnmuteTime());
        }
    }

    @EventHandler
    public void onUnmute(UnmuteEvent unmuteEvent) {
        CoreUser coreUser = userManager.getUser(Bukkit.getPlayer(unmuteEvent.getTarget()).getUniqueId());

        if (coreUser != null) {
            Bukkit.getPlayer(unmuteEvent.getTarget()).sendMessage(ChatColor.AQUA + "You have been unmuted.");
            coreUser.setMuted(false);
            coreUser.setUnMuteTime(unmuteEvent.getUnmuteTime());
        }
    }
}
