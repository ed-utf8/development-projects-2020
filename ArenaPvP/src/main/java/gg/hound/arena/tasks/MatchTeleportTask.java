package gg.hound.arena.tasks;

import gg.hound.arena.hologram.HologramManager;
import gg.hound.arena.scoreboard.ScoreboardManager;
import gg.hound.arena.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Queue;

public class MatchTeleportTask extends BukkitRunnable {

    private final InventoryUtil inventoryUtil;
    private final ScoreboardManager scoreboardManager;
    private final HologramManager hologramManager;

    private final Queue<Player> backToSpawn = new ArrayDeque<>();

    private final Location spawn = Bukkit.getWorld("training").getSpawnLocation();

    public MatchTeleportTask(InventoryUtil inventoryUtil, ScoreboardManager scoreboardManager, HologramManager hologramManager) {
        this.inventoryUtil = inventoryUtil;
        this.scoreboardManager = scoreboardManager;
        this.hologramManager = hologramManager;
    }

    @Override
    public void run() {
        if (backToSpawn.size() != 0) {
            Player player;
            for (int i = 0; i < 10; i++) {
                player = backToSpawn.poll();

                if (player == null)
                    return;

                player.setFlying(false);
                player.setAllowFlight(false);

                player.getInventory().clear();
                player.getInventory().setArmorContents(null);

                for (PotionEffect potionEffect : player.getActivePotionEffects())
                    player.removePotionEffect(potionEffect.getType());

                inventoryUtil.giveSpawnInventory(player);

                scoreboardManager.setScoreboard(player);

                player.teleport(spawn);

                hologramManager.sendDefaultHolograms(player);
            }
        }
    }

    public void teleportToSpawn(Player player) {
        backToSpawn.add(player);
    }
}
