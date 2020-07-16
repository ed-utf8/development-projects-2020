package gg.hound.core.listeners;

import gg.hound.core.events.AdminChatEvent;
import gg.hound.core.events.DisguisePlayerJoinEvent;
import gg.hound.core.events.StaffChatEvent;
import gg.hound.core.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StaffChatListener implements Listener {

    private final UserManager userManager;

    public StaffChatListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onStaffChat(StaffChatEvent staffChatEvent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission("core.staff")) {
                if (userManager.getUser(player.getUniqueId()).isStaffNotifications()) {
                    player.sendMessage(
                        "§4[STAFF]§f[§7MC§f]" +
                            "[§7" + staffChatEvent.getStaffServer() + "§f] " +
                            "§9" + staffChatEvent.getStaffUsername() + "§7: §f" +
                            staffChatEvent.getStaffMessage()
                    );
                }
            }
        }
    }

    @EventHandler
    public void onAdminChat(AdminChatEvent adminChatEvent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission("core.admin")) {
                if (userManager.getUser(player.getUniqueId()).isStaffNotifications()) {
                    player.sendMessage(
                        "§4[STAFF]§f[§cAC§f]" +
                            "[§7" + adminChatEvent.getStaffServer() + "§f] " +
                            "§c" + adminChatEvent.getStaffUsername() + "§7: §f" +
                            adminChatEvent.getStaffMessage()
                    );
                }
            }
        }
    }

    @EventHandler
    public void onDisguisePlayerJoin(DisguisePlayerJoinEvent disguisePlayerJoinEvent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission("core.staff")) {
                if (userManager.getUser(player.getUniqueId()).isStaffNotifications()) {
                    player.sendMessage(
                        "§4[STAFF]§f[§eDISGUISE§f]" +
                            "§c" + disguisePlayerJoinEvent.getRealName() + "§f has joined with disguise§7: §b" +
                            disguisePlayerJoinEvent.getDisguiseName()
                    );
                }
            }
        }
    }

}
