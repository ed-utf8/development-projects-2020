package gg.hound.arena.user;

import gg.hound.arena.Arena;
import gg.hound.arena.util.InventoryUtil;
import gg.hound.arena.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectatorManager {

    private final Arena arena;
    private final InventoryUtil inventoryUtil;

    public SpectatorManager(Arena arena, InventoryUtil inventoryUtil) {
        this.arena = arena;
        this.inventoryUtil = inventoryUtil;
    }

    private final List<UUID> spectators = new ArrayList<>();

    public List<UUID> getSpectators() {
        return spectators;
    }

    public void addSpectator(Player player, User user) {
        user.setUserState(UserState.SPECTATOR);

        player.sendMessage(arena.getPrefix() + "Now in spectator mode");
        for (Player online : Bukkit.getServer().getOnlinePlayers())
            online.hidePlayer(player);

        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        playerInventory.setArmorContents(null);

        playerInventory.setItem(8, new ItemBuilder(Material.REDSTONE_TORCH_ON).setName(ChatColor.YELLOW + "Exit Spectator Mode").toItemStack());

        player.setExp(0);
        player.setHealth(20.0);
        player.setSaturation(20);
        player.setFireTicks(0);

        player.setAllowFlight(true);
        player.setFlying(true);

        player.spigot().setCollidesWithEntities(false);

        spectators.add(player.getUniqueId());
    }

    public void removeSpectator(Player player, User user) {
        user.setUserState(UserState.LOBBY);

        player.sendMessage(arena.getPrefix() + "You have left spectator mode.");
        for (Player online : Bukkit.getServer().getOnlinePlayers())
            online.showPlayer(player);

        player.setExp(0);
        player.setHealth(20.0);
        player.setSaturation(20);
        player.setFireTicks(0);

        player.spigot().setCollidesWithEntities(true);

        player.setFlying(false);
        player.setAllowFlight(false);

        inventoryUtil.giveSpawnInventory(player);

        spectators.remove(player.getUniqueId());

        if (!player.getWorld().getName().equals("training"))
            player.teleport(Bukkit.getWorld("training").getSpawnLocation());
    }
}
