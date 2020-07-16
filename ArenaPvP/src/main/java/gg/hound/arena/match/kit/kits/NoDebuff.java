package gg.hound.arena.match.kit.kits;

import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NoDebuff implements Kit {

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public String getName() {
        return "No-Debuff";
    }

    @Override
    public boolean isRegen() {
        return true;
    }

    @Override
    public boolean isBuilding() {
        return false;
    }

    @Override
    public boolean isHunger() {
        return true;
    }

    @Override
    public ItemStack getInventoryItem() {
        return new ItemBuilder(Material.POTION).setDurability((short) 16421).setName("§a" + getName()).toItemStack();
    }

    @Override
    public ItemStack[] getArmourContents() {
        return new ItemStack[] {
                new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack(),
                new ItemBuilder(Material.DIAMOND_LEGGINGS).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack(),
                new ItemBuilder(Material.DIAMOND_HELMET).addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).toItemStack()
        };
    }

    private final ItemStack heal = new ItemBuilder(Material.POTION).setDurability((short) 16421).toItemStack();

    @Override
    public void giveKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.FIRE_ASPECT, 2).addEnchant(Enchantment.DAMAGE_ALL, 3).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.ENDER_PEARL, 16).toItemStack());
        playerInventory.setItem(2, new ItemBuilder(Material.COOKED_BEEF, 64).toItemStack());
        playerInventory.setItem(7,  new ItemBuilder(Material.POTION).setDurability((short) 8259).addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9600, 1), false).toItemStack());
        playerInventory.setItem(8, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
        playerInventory.setItem(17, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
        playerInventory.setItem(26, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
        playerInventory.setItem(35,new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
        for (int i = 0; i < 36; i++) {
            if (playerInventory.getItem(i) == null)
                playerInventory.setItem(i, heal);
        }
    }
}
