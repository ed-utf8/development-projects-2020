package gg.hound.bungeecore.listeners;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.maintainence.MaintenanceMode;
import gg.hound.bungeecore.servermanager.ServerManager;
import gg.hound.bungeecore.sql.SQLManager;
import gg.hound.bungeecore.user.BungeeCoreUser;
import gg.hound.bungeecore.user.UserManager;
import gg.hound.bungeecore.util.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class PostLoginListener implements Listener {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final UserManager userManager;
    private final SQLManager sqlManager;
    private final MaintenanceMode maintenanceMode;
    private final ServerManager serverManager;

    private final String maintenanceLines = "§f⚔ §c§lHound Network §f⚔ \n" +
            "§c§lHound Network §cis currently under maintenance";

    public PostLoginListener(BungeeCorePlugin bungeeCorePlugin, UserManager userManager, SQLManager sqlManager, MaintenanceMode maintenanceMode, ServerManager serverManager) {
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.userManager = userManager;
        this.sqlManager = sqlManager;
        this.maintenanceMode = maintenanceMode;
        this.serverManager = serverManager;
    }

    @EventHandler(priority = 5)
    public void onLoginToProxy(LoginEvent loginEvent) {

        if (loginEvent.getConnection().getVersion() < 47) {
            loginEvent.setCancelled(true);
            loginEvent.setCancelReason(new ComponentBuilder("§cOutdated Version, we have moved to §41.8 - 1.15").create());
            return;
        }

        loginEvent.registerIntent(bungeeCorePlugin);

        ProxyServer.getInstance().getScheduler().runAsync(bungeeCorePlugin, () -> {

            UserConnection userConnection = sqlManager.logIp(loginEvent.getConnection().getAddress().getAddress().getHostAddress());

            if (userConnection == null) {
                loginEvent.setCancelled(true);
                loginEvent.setCancelReason(new ComponentBuilder(ChatColor.DARK_RED + "Internal Error Occurred: " + ChatColor.WHITE + "IP_FAULT_1").create());
                loginEvent.completeIntent(bungeeCorePlugin);
                return;
            }

            if (userConnection.isVpn()) {
                loginEvent.setCancelled(true);
                loginEvent.setCancelReason(new ComponentBuilder(ChatColor.DARK_RED + "Hound Anti-VPN: " + ChatColor.WHITE + "Connection Refused, please disconnect from your VPN.").create());
                loginEvent.completeIntent(bungeeCorePlugin);
                return;
            }

            BungeeCoreUser coreUser = sqlManager.loadUser(loginEvent.getConnection().getUniqueId(), loginEvent.getConnection().getName(), userConnection.getIpId());

            if (coreUser == null) {
                loginEvent.setCancelled(true);
                loginEvent.setCancelReason(new ComponentBuilder(ChatColor.DARK_RED + "Internal Error Occurred: " + ChatColor.WHITE + "USER_LOAD_FAULT_1").create());
                loginEvent.completeIntent(bungeeCorePlugin);
                return;
            }

            if (maintenanceMode.isMaintenanceMode()) {

                if (!maintenanceMode.getWhitelist().contains(coreUser.getUuid())) {
                    if (!coreUser.getGroups().contains("admin") && !coreUser.getGroups().contains("owner") && !coreUser.getGroups().contains("developer")) {
                        loginEvent.setCancelled(true);
                        loginEvent.setCancelReason(new ComponentBuilder(maintenanceLines).create());
                        userManager.clearUser(coreUser.getUuid());
                    }
                }
            }

            loginEvent.completeIntent(bungeeCorePlugin);
        });

    }

    @EventHandler(priority = 5)
    public void onPostLogin(PostLoginEvent postLoginEvent) {
        ServerInfo serverInfo = serverManager.getRandomHub();
        postLoginEvent.getPlayer().setReconnectServer(serverInfo);
        BungeeCoreUser coreUser = userManager.getUser(postLoginEvent.getPlayer().getUniqueId());

        for (String group : coreUser.getGroups()) {
            if (group.equalsIgnoreCase("owner")) {
                postLoginEvent.getPlayer().addGroups("staff", "admin");
            }

            if (group.equalsIgnoreCase("admin")) {
                postLoginEvent.getPlayer().addGroups("staff", "admin");
            }

            if (group.equalsIgnoreCase("manager")) {
                postLoginEvent.getPlayer().addGroups("staff", "manager");
            }

            if (group.equalsIgnoreCase("developer")) {
                postLoginEvent.getPlayer().addGroups("admin", "staff", "developer");
            }

            if (group.contains("staff")) {
                postLoginEvent.getPlayer().addGroups("staff");
            }

            if (group.contains("private-server")) {
                postLoginEvent.getPlayer().addGroups("private-server");
            }

            if (group.equalsIgnoreCase("streamer") || group.equalsIgnoreCase("youtube") || group.equalsIgnoreCase("famous")) {
                postLoginEvent.getPlayer().addGroups("media");
            }
        }

    }


    @EventHandler
    public void onLeave(PlayerDisconnectEvent disconnectEvent) {
        userManager.clearUser(disconnectEvent.getPlayer().getUniqueId());
    }
}
