package gg.hound.arena.match.kit;

import gg.hound.arena.util.ItemBuilder;
import gg.hound.arena.util.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {

    private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    private final RandomCollection<Enchantment> armourEnchantments = new RandomCollection<Enchantment>(threadLocalRandom)
            .add(80, Enchantment.PROTECTION_ENVIRONMENTAL)
            .add(4, Enchantment.PROTECTION_PROJECTILE)
            .add(4, Enchantment.PROTECTION_FIRE)
            .add(4, Enchantment.THORNS)
            .add(4, Enchantment.PROTECTION_PROJECTILE)
            .add(4, null);

    private final RandomCollection<Enchantment> swordEnchantments = new RandomCollection<Enchantment>(threadLocalRandom)
            .add(80, Enchantment.DAMAGE_ALL)
            .add(4, Enchantment.DAMAGE_ARTHROPODS)
            .add(4, Enchantment.DAMAGE_UNDEAD)
            .add(4, Enchantment.KNOCKBACK)
            .add(4, Enchantment.FIRE_ASPECT)
            .add(4, null);

    private final RandomCollection<Enchantment> bowEnchantments = new RandomCollection<Enchantment>(threadLocalRandom)
            .add(40, Enchantment.ARROW_DAMAGE)
            .add(40, Enchantment.ARROW_KNOCKBACK)
            .add(20, null);

    private final RandomCollection<Material> helmets = new RandomCollection<Material>(threadLocalRandom)
            .add(50, Material.DIAMOND_HELMET)
            .add(50, Material.IRON_HELMET);

    private final RandomCollection<Material> chestplates = new RandomCollection<Material>(threadLocalRandom)
            .add(50, Material.DIAMOND_CHESTPLATE)
            .add(50, Material.IRON_CHESTPLATE);

    private final RandomCollection<Material> leggings = new RandomCollection<Material>(threadLocalRandom)
            .add(50, Material.DIAMOND_LEGGINGS)
            .add(50, Material.IRON_LEGGINGS);

    private final RandomCollection<Material> boots = new RandomCollection<Material>(threadLocalRandom)
            .add(50, Material.DIAMOND_BOOTS)
            .add(50, Material.IRON_BOOTS);

    private final RandomCollection<Material> swords = new RandomCollection<Material>(threadLocalRandom)
            .add(50, Material.IRON_SWORD)
            .add(50, Material.DIAMOND_SWORD);

    private final ItemStack healing = new ItemBuilder(Material.POTION).setDurability((short) 16421).toItemStack();

    public boolean giveKit(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        playerInventory.clear();

        Material material = helmets.next();
        Enchantment[] enchantments = getEnchantments(2, armourEnchantments);

        playerInventory.setHelmet(applyEnchantments(material, enchantments));
        playerInventory.setChestplate(applyEnchantments(chestplates.next(), getEnchantments(2, armourEnchantments)));
        playerInventory.setLeggings(applyEnchantments(leggings.next(), getEnchantments(2, armourEnchantments)));
        playerInventory.setBoots(applyEnchantments(boots.next(), getEnchantments(2, armourEnchantments)));

        playerInventory.setItem(0, applyEnchantments(swords.next(), getEnchantments(3, swordEnchantments)));
        boolean build = (threadLocalRandom.nextDouble(1) > 0.5);
        if (build) {
            playerInventory.setItem(1, new ItemBuilder(Material.FISHING_ROD).setUnbreakable(true).toItemStack());
            ItemStack bow = applyEnchantments(Material.BOW, getEnchantments(2, bowEnchantments));
            playerInventory.setItem(2, bow);
            if (bow.getEnchantments().containsKey(Enchantment.ARROW_INFINITE))
                playerInventory.setItem(29, new ItemBuilder(Material.ARROW, 1).toItemStack());
            else
                playerInventory.setItem(29, new ItemBuilder(Material.ARROW, threadLocalRandom.nextInt(8, 32)).toItemStack());
            playerInventory.setItem(3, new ItemBuilder(Material.GOLDEN_APPLE, threadLocalRandom.nextInt(1,33)).toItemStack());
            playerInventory.setItem(4, new ItemBuilder(Material.GOLDEN_APPLE, threadLocalRandom.nextInt(1,4)).setName("§6Golden Head").toItemStack());
            playerInventory.setItem(5, new ItemBuilder(Material.COBBLESTONE, threadLocalRandom.nextInt(32, 64)).toItemStack());
            playerInventory.setItem(6, new ItemBuilder(Material.DIAMOND_PICKAXE).setUnbreakable(true).toItemStack());
            playerInventory.setItem(7, new ItemBuilder((threadLocalRandom.nextInt(100) > 50 ? Material.LAVA_BUCKET : Material.FLINT_AND_STEEL)).toItemStack());
            playerInventory.setItem(8, new ItemBuilder(Material.WATER_BUCKET).toItemStack());
        } else {
            playerInventory.setItem(1, new ItemBuilder(Material.ENDER_PEARL, 16).toItemStack());
            playerInventory.setItem(2, new ItemBuilder(Material.COOKED_BEEF, 64).toItemStack());
            playerInventory.setItem(6, new ItemBuilder(Material.DIAMOND_PICKAXE).setUnbreakable(true).toItemStack());
            playerInventory.setItem(7, new ItemBuilder(Material.POTION).setDurability((short) 8259).addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9600, 1), false).toItemStack());
            playerInventory.setItem(8, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
            playerInventory.setItem(17, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
            playerInventory.setItem(26, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());
            playerInventory.setItem(35, new ItemBuilder(Material.POTION).setDurability((short) 8226).addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1800, 1), false).toItemStack());

            for (int i = 0; i <= threadLocalRandom.nextInt(5, 15); i++)
                playerInventory.addItem(healing);
        }

        return build;
    }

    private Enchantment[] getEnchantments(int size, RandomCollection<Enchantment> randomCollection) {
        Enchantment[] enchantments = new Enchantment[size];
        double chance = 100D;

        for (int i = 0; i < enchantments.length; i++) {
            if (threadLocalRandom.nextDouble(100D) <= chance) {
                Enchantment enchantment = randomCollection.next();
                if (enchantment == null)
                    break;
                enchantments[i] = enchantment;
            }

            chance /= (2 * (i + 1));
        }
        return enchantments;
    }

    private ItemStack applyEnchantments(Material material, Enchantment[] enchantments) {
        ItemStack itemStack = new ItemStack(material);
        for (Enchantment enchantment : enchantments) {
            if (enchantment == null)
                break;

            itemStack.addEnchantment(enchantment, threadLocalRandom.nextInt(1, enchantment.getMaxLevel()));
        }
        return itemStack;
    }


}
