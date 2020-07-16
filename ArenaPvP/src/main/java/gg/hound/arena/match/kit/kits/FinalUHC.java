package gg.hound.arena.match.kit.kits;

import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FinalUHC implements Kit {

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getName() {
        return "FinalUHC";
    }

    @Override
    public boolean isRegen() {
        return false;
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
        return new ItemBuilder(Material.GOLDEN_APPLE).setName("§a" + getName()).toItemStack();
    }

    @Override
    public ItemStack[] getArmourContents() {
        return new ItemStack[] {
                new ItemBuilder(Material.DIAMOND_BOOTS).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack(),
                new ItemBuilder(Material.DIAMOND_HELMET).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack(),
        };
    }

    @Override
    public void giveKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 4).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.FISHING_ROD).toItemStack());
        playerInventory.setItem(2, new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 3).toItemStack());
        playerInventory.setItem(3, new ItemBuilder(Material.COBBLESTONE).setAmount(64).toItemStack());
        playerInventory.setItem(4, new ItemBuilder(Material.GOLDEN_APPLE).setAmount(33).toItemStack());
        playerInventory.setItem(5, new ItemBuilder(Material.GOLDEN_APPLE, 3).setName("§6Golden Head").toItemStack());
        playerInventory.setItem(6, new ItemBuilder(Material.COOKED_BEEF).setAmount(64).toItemStack());
        playerInventory.setItem(7, new ItemBuilder(Material.LAVA_BUCKET).toItemStack());
        playerInventory.setItem(8, new ItemBuilder(Material.WATER_BUCKET).toItemStack());

        playerInventory.setItem(18, new ItemBuilder(Material.DIAMOND_HELMET).setDurability((short) 150).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).toItemStack());
        playerInventory.setItem(19, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).toItemStack());
        playerInventory.setItem(20, new ItemBuilder(Material.DIAMOND_LEGGINGS).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).toItemStack());
        playerInventory.setItem(21, new ItemBuilder(Material.DIAMOND_BOOTS).setDurability((short) 150).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).toItemStack());

        playerInventory.setItem(25, new ItemBuilder(Material.FLINT_AND_STEEL).toItemStack());
        playerInventory.setItem(26, new ItemBuilder(Material.WATER_BUCKET).toItemStack());

        playerInventory.setItem(9, new ItemBuilder(Material.DIAMOND_HELMET).setDurability((short) 200).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack());
        playerInventory.setItem(10, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDurability((short) 350).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack());
        playerInventory.setItem(11, new ItemBuilder(Material.DIAMOND_LEGGINGS).setDurability((short) 350).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack());
        playerInventory.setItem(12, new ItemBuilder(Material.DIAMOND_BOOTS).setDurability((short) 250).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack());

        playerInventory.setItem(27, new ItemBuilder(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.DIG_SPEED, 3).toItemStack());
        playerInventory.setItem(28, new ItemBuilder(Material.COBBLESTONE).setAmount(64).toItemStack());
        playerInventory.setItem(29, new ItemBuilder(Material.COBBLESTONE).setAmount(64).toItemStack());
        playerInventory.setItem(30, new ItemBuilder(Material.COBBLESTONE).setAmount(64).toItemStack());
        playerInventory.setItem(31, new ItemBuilder(Material.ARROW).setAmount(48).toItemStack());

        playerInventory.setItem(34, new ItemBuilder(Material.LAVA_BUCKET).toItemStack());
        playerInventory.setItem(35, new ItemBuilder(Material.WATER_BUCKET).toItemStack());

    }
}
