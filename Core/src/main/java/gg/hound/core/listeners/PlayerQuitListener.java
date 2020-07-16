package gg.hound.core.listeners;

import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final UserManager userManager;
    private final PunishmentData punishmentData;
    private final PluginUtils pluginUtils;

    public PlayerQuitListener(UserManager userManager, PunishmentData punishmentData, PluginUtils pluginUtils) {
        this.userManager = userManager;
        this.punishmentData = punishmentData;

        this.pluginUtils = pluginUtils;
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        playerQuitEvent.setQuitMessage(null);
        playerQuitEvent.getPlayer().setOp(false);

        userManager.removeUser(playerQuitEvent.getPlayer().getUniqueId());

        punishmentData.removeOtherBan(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeOtherReport(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeOtherMute(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeOtherIPBan(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeMute(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeBan(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeIPBan(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeKick(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeOtherKick(playerQuitEvent.getPlayer().getUniqueId());

        pluginUtils.removeColour(playerQuitEvent.getPlayer().getUniqueId());
        punishmentData.removeAutomutedPlayer(playerQuitEvent.getPlayer().getUniqueId());

    }
}
