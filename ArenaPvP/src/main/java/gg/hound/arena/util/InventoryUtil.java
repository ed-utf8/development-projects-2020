package gg.hound.arena.util;

import gg.hound.arena.match.MatchManager;
import gg.hound.arena.match.duel.Duel;
import gg.hound.arena.match.duel.DuelType;
import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryUtil {

    private final KitManager kitManager;
    private final MatchManager matchManager;

    public InventoryUtil(KitManager kitManager, MatchManager matchManager) {
        this.kitManager = kitManager;
        this.matchManager = matchManager;
    }

    private final ItemStack backItem = new ItemBuilder(Material.WOOL).setWoolColour(DyeColor.RED).setName(ChatColor.YELLOW + "Go Back").toItemStack();

    public void giveSpawnInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        playerInventory.clear();

        playerInventory.setArmorContents(null);

        playerInventory.setItem(0, new ItemBuilder(Material.GOLD_SWORD).setName(ChatColor.YELLOW + "Randomizer Queue").setUnbreakable(true).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatColor.YELLOW + "Ranked 1v1").setUnbreakable(true).toItemStack());
        playerInventory.setItem(2, new ItemBuilder(Material.IRON_SWORD).setName(ChatColor.YELLOW + "Unranked 1v1").setUnbreakable(true).toItemStack());

        playerInventory.setItem(4, new ItemBuilder(Material.EYE_OF_ENDER).setName(ChatColor.YELLOW + "Spectate").toItemStack());

        playerInventory.setItem(6, new ItemBuilder(Material.PAPER).setName(ChatColor.YELLOW + "Create a party").toItemStack());
        playerInventory.setItem(7, new ItemBuilder(Material.BOOK).setName(ChatColor.YELLOW + "Edit Kits").toItemStack());
        playerInventory.setItem(8, new ItemBuilder(Material.CHEST).setName(ChatColor.YELLOW + "Settings").toItemStack());

        player.updateInventory();
    }

    public void givePartyLeaderInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();

        playerInventory.setArmorContents(null);

        playerInventory.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).setName(ChatColor.YELLOW + "Ranked 2v2").setUnbreakable(true).toItemStack());
        playerInventory.setItem(1, new ItemBuilder(Material.IRON_SWORD).setName(ChatColor.YELLOW + "Unranked 2v2").setUnbreakable(true).toItemStack());

        playerInventory.setItem(8, new ItemBuilder(Material.REDSTONE_TORCH_ON).setName(ChatColor.YELLOW + "Leave Party").toItemStack());


        player.updateInventory();
    }

    public Inventory getRankedInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Ranked");
        if (kitManager.getKits().size() == 0)
            inventory.addItem(new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "Coming soon...").toItemStack());

        for (Kit kit : kitManager.getKits()) {
            if (kit != null) {
                ItemStack itemStack = kit.getInventoryItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("§eGame Info:");
                lore.add("§bIn Queue§8: " + matchManager.getSoloRankedQueue(kit).size());
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                inventory.addItem(itemStack);
            }
        }
        return inventory;
    }

    public Inventory getUnrankedInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Unranked");
        if (kitManager.getKits().size() == 0)
            inventory.addItem(new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "Coming soon...").toItemStack());

        for (Kit kit : kitManager.getKits()) {
            if (kit != null) {
                ItemStack itemStack = kit.getInventoryItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("§eGame Info:");
                lore.add("§bIn Queue§8: " + matchManager.getSoloUnrankedQueue(kit).size());
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                inventory.addItem(itemStack);
            }
        }
        return inventory;
    }

    public Inventory getSettingsInventory(User user) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Settings");
        inventory.setItem(12, new ItemBuilder(Material.IRON_SWORD).setName(ChatColor.YELLOW + "Duel Requests")
                .setLore(null, ChatColor.AQUA + "Your dual requests are currently: " + (user.isDuelRequests() ?
                        ChatColor.GREEN + "Enabled" :  ChatColor.RED + "Disabled")).toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.PAPER).setName(ChatColor.YELLOW + "Party Requests")
                .setLore(null, ChatColor.AQUA + "Your party requests are currently: " + (user.isPartyRequests()
                        ? ChatColor.GREEN + "Enabled" :  ChatColor.RED + "Disabled")).toItemStack());
        return inventory;
    }

    public Inventory duelCreationInventory(Duel duel) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Duel Creator");
        inventory.setItem(11, new ItemBuilder(Material.CHEST).setName(ChatColor.YELLOW +  "Kit").setLore(duel.getKit().getName()).toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.SKULL).setSkullOwner(Bukkit.getPlayer(duel.getTarget()).getName()).setName(ChatColor.YELLOW + Bukkit.getPlayer(duel.getTarget()).getName()).toItemStack());
        inventory.setItem(15, new ItemBuilder(Material.PAPER).setName(ChatColor.YELLOW + "Number of Matches").setLore(ChatColor.YELLOW + duel.getDuelType().getName()).toItemStack());
        inventory.setItem(26, new ItemBuilder(Material.WOOL).setWoolColour(DyeColor.LIME).setName(ChatColor.YELLOW + "Send Duel Request").toItemStack());
        return inventory;
    }

    public Inventory kitSelection(Duel duel) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Kit");
        if (kitManager.getKits().size() == 0)
            inventory.addItem(new ItemBuilder(Material.PAPER).setName(ChatColor.GOLD + "Coming soon...").toItemStack());

        for (Kit kit : kitManager.getKits()) {
            if (kit == duel.getKit()) {
                ItemStack itemStack = kit.getInventoryItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + "Selected");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                inventory.addItem(itemStack);
            } else inventory.addItem(kit.getInventoryItem());
        }

        inventory.setItem(8, backItem);
        return inventory;
    }

    public Inventory numberOfMatches(Duel duel) {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Number Of Matches");
        for (DuelType duelType : DuelType.values()) {
            if (duelType == duel.getDuelType())
                inventory.addItem(new ItemBuilder(Material.CHEST).setName(ChatColor.YELLOW + duelType.getName()).setLore(ChatColor.GREEN + "Selected").toItemStack());
            else inventory.addItem(new ItemBuilder(Material.CHEST).setName(ChatColor.YELLOW + duelType.getName()).toItemStack());
        }

        inventory.setItem(8, backItem);
        return inventory;
    }

    public Inventory spectatorInventory() {
        Inventory inventory = Bukkit.createInventory(null, Math.min(54, (int) (9*(Math.ceil(Math.abs(matchManager.getMatchMap().size()/9))))), ChatColor.AQUA + "Spectate Matches");
        System.out.println("Map Size: " + matchManager.getMatchMap().size());
        for (UUID uuid : matchManager.getMatchMap().keySet()) {
            System.out.println("UUID: " + uuid);
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                continue;

            inventory.addItem(new ItemBuilder(Material.SKULL).setSkullOwner(player.getName()).setName(ChatColor.YELLOW + player.getName()).toItemStack());
        }

        return inventory;
    }

    public Inventory kitEditor() {
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Edit your kit");
        for (Kit kit : kitManager.getKits())
            inventory.addItem(kit.getInventoryItem());
        return inventory;
    }
}
