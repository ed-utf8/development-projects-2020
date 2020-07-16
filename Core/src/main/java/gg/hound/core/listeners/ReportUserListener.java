package gg.hound.core.listeners;

import gg.hound.core.events.ReportUserEvent;
import gg.hound.core.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReportUserListener implements Listener {

    private final UserManager userManager;

    public ReportUserListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onReportUser(ReportUserEvent reportUserEvent) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.hasPermission("core.staff")) {
                if (userManager.getUser(player.getUniqueId()).isStaffNotifications()) {
                    player.sendMessage(
                        "§f[§9REPORT§f]" +
                            "[§7" + reportUserEvent.getUserServer() + "§f] " +
                            "§9" + reportUserEvent.getUserName() + "§7 has reported §c" +
                            reportUserEvent.getUserReported() + "§7 for §b" +
                            reportUserEvent.getUserReportedReason()
                    );
                }
            }
        }
    }
}
