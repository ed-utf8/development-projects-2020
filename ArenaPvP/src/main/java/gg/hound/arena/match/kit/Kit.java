package gg.hound.arena.match.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Kit {

    int getId();

    String getName();

    boolean isRegen();

    boolean isBuilding();

    boolean isHunger();

    ItemStack getInventoryItem();

    ItemStack[] getArmourContents();

    void giveKit(Player player);
}
