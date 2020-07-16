package gg.hound.arena.listeners;

import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.user.UserState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ArenaListeners implements Listener {

    private final UserManager userManager;

    public ArenaListeners(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.MATCH) {
            blockPlaceEvent.setCancelled(true);
            return;
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.MATCH) {
            blockBreakEvent.setCancelled(true);
            return;
        }

        if (blockBreakEvent.getBlock().getType() != Material.COBBLESTONE && blockBreakEvent.getBlock().getType() != Material.OBSIDIAN)
            blockBreakEvent.setCancelled(true);
    }
}
