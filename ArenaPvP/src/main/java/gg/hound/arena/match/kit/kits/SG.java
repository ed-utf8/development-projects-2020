package gg.hound.arena.match.kit.kits;

import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SG implements Kit {

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public String getName() {
        return "SG";
    }

    @Override
    public boolean isRegen() {
        return true;
    }

    @Override
    public boolean isBuilding() {
        return true;
    }

    @Override
    public boolean isHunger() {
        return true;
    }

    @Override
    public ItemStack getInventoryItem() {
        return new ItemBuilder(Material.STONE_SWORD).setName("§a" + getName()).toItemStack();
    }

    @Override
    public ItemStack[] getArmourContents() {
        return new ItemStack[] {
                new ItemBuilder(Material.GOLD_BOOTS).toItemStack(),
                new ItemBuilder(Material.IRON_LEGGINGS).toItemStack(),
                new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).toItemStack(),
                new ItemBuilder(Material.IRON_HELMET).toItemStack()
        };
    }

    @Override
    public void giveKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setItem(0, new ItemBuilder(Material.STONE_SWORD).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.FISHING_ROD).toItemStack());
        playerInventory.setItem(2, new ItemBuilder(Material.FLINT_AND_STEEL).setDurability((short) 61).toItemStack());
        playerInventory.setItem(3, new ItemBuilder(Material.BOW).toItemStack());
        playerInventory.setItem(4, new ItemBuilder(Material.ARROW, 7).toItemStack());
        playerInventory.setItem(5, new ItemBuilder(Material.GOLDEN_APPLE).toItemStack());
        playerInventory.setItem(6, new ItemBuilder(Material.BREAD, 4).toItemStack());
        playerInventory.setItem(7, new ItemBuilder(Material.PUMPKIN_PIE, 2).toItemStack());
        playerInventory.setItem(8, new ItemBuilder(Material.COOKED_BEEF, 3).toItemStack());
    }
}
