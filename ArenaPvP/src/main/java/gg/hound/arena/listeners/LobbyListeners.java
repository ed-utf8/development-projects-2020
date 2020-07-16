package gg.hound.arena.listeners;

import gg.hound.arena.Arena;
import gg.hound.arena.hologram.HologramManager;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.match.duel.Duel;
import gg.hound.arena.match.duel.DuelManager;
import gg.hound.arena.match.duel.DuelType;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.user.SpectatorManager;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.user.UserState;
import gg.hound.arena.user.party.Party;
import gg.hound.arena.util.InventoryUtil;
import gg.hound.arena.util.ItemBuilder;
import gg.hound.arena.util.PacketUtil;
import gg.hound.spigot.event.ArmourEquipEvent;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class LobbyListeners implements Listener {

    private final Arena arena;
    private final UserManager userManager;
    private final InventoryUtil inventoryUtil;
    private final SpectatorManager spectatorManager;
    private final KitManager kitManager;
    private final PacketUtil packetUtil;
    private final MatchManager matchManager;
    private final DuelManager duelManager;
    private final HologramManager hologramManager;

    public LobbyListeners(Arena arena, UserManager userManager, InventoryUtil inventoryUtil, SpectatorManager spectatorManager, KitManager kitManager, PacketUtil packetUtil, MatchManager matchManager, DuelManager duelManager, HologramManager hologramManager) {
        this.arena = arena;
        this.userManager = userManager;
        this.inventoryUtil = inventoryUtil;
        this.spectatorManager = spectatorManager;
        this.kitManager = kitManager;
        this.packetUtil = packetUtil;
        this.matchManager = matchManager;
        this.duelManager = duelManager;
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.LOBBY && user.getUserState() != UserState.SPECTATOR)
            return;


        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (playerInteractEvent.getClickedBlock().getState().getData() instanceof Openable)
                playerInteractEvent.setCancelled(true);

            if (playerInteractEvent.getClickedBlock().getState().getData() instanceof InventoryHolder)
                playerInteractEvent.setCancelled(true);
        }

        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        if (playerInteractEvent.getItem() == null)
            return;

        if (playerInteractEvent.getItem().getType() == null)
            return;

        playerInteractEvent.setCancelled(true);

        if (kitManager.isEditingKit(player.getUniqueId()))
            return;

        switch (playerInteractEvent.getItem().getType()) {
            case GOLD_SWORD: {
                matchManager.handleRandomizerQueue(player);
                break;
            }
            case DIAMOND_SWORD: {
                player.openInventory(inventoryUtil.getRankedInventory());
                break;
            }
            case IRON_SWORD: {
                player.openInventory(inventoryUtil.getUnrankedInventory());
                break;
            }
            case BOOK: {
                player.openInventory(inventoryUtil.kitEditor());
                break;
            }
            case PAPER: {
                player.sendMessage(arena.getPrefix() + "Temporarily disabled.");
                /*Party party = new Party(player.getUniqueId());
                user.setParty(party);
                inventoryUtil.givePartyLeaderInventory(player);

                player.sendMessage(arena.getPrefix() + "Successfully created a party.");*/
                break;
            }
            case CHEST: {
                player.openInventory(inventoryUtil.getSettingsInventory(user));
                break;
            }
            case EYE_OF_ENDER: {
                spectatorManager.addSpectator(player, user);
                break;
            }
            case REDSTONE_TORCH_ON: {
                if (user.getUserState() == UserState.SPECTATOR)
                    spectatorManager.removeSpectator(player, user);
                else if (user.getParty() != null) {
                    Party party = user.getParty();
                    if (party.getPartyMembers().size() > 1)
                        party.removePartyMember(player.getUniqueId());
                    user.setParty(null);
                    inventoryUtil.giveSpawnInventory(player);
                    player.sendMessage(arena.getPrefix() + "Successfully left the party");
                }
                else {
                    matchManager.removeFromQueue(player.getUniqueId());
                    packetUtil.sendActionBar(player, ChatColor.YELLOW + "Successfully left the queue.");
                    inventoryUtil.giveSpawnInventory(player);
                }
                break;
            }
            case COMPASS: {
                if (user.getUserState() != UserState.SPECTATOR)
                    return;

                player.openInventory(inventoryUtil.spectatorInventory());
                break;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getCurrentItem() == null)
            return;

        if (inventoryClickEvent.getCurrentItem().getType() == null)
            return;

        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if (kitManager.isEditingKit(player.getUniqueId())) {
            if (inventoryClickEvent.getSlotType() == null)
                return;

            if (inventoryClickEvent.getSlotType() == InventoryType.SlotType.ARMOR) {
                inventoryClickEvent.setCancelled(true);
                return;
            }
            return;
        }

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.LOBBY)
            return;

        inventoryClickEvent.setCancelled(true);

        switch (inventoryClickEvent.getInventory().getName().replace("§b", "")) {
            case "Ranked": {
                player.closeInventory();

                Party party = user.getParty();
                if (party != null) {
                    matchManager.handlePartyQueue(party, kitManager.getKit(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName()), true);
                    return;
                }

                if (user.getMatchesRemaining() != 0)
                    matchManager.handleSoloQueue(player, kitManager.getKit(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName()), true);
                else
                    player.sendMessage(arena.getPrefix() + "You have insufficient matches remaining. Purchase a rank to get unlimited.");
                break;
            }
            case "Unranked": {
                player.closeInventory();

                Party party = user.getParty();
                if (party != null) {
                    matchManager.handlePartyQueue(party, kitManager.getKit(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName()), false);
                    return;
                }
                matchManager.handleSoloQueue(player, kitManager.getKit(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName()), false);
                break;
            }
            case "Settings": {
                switch (inventoryClickEvent.getCurrentItem().getType()) {
                    case PAPER: {
                        user.setPartyRequests(!user.isPartyRequests());
                        inventoryClickEvent.getInventory().setItem(14, new ItemBuilder(Material.PAPER).setName(ChatColor.YELLOW + "Party Requests")
                                .setLore(null, ChatColor.AQUA + "Your party requests are currently: " + (user.isPartyRequests() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled")).toItemStack());
                        break;
                    }
                    case IRON_SWORD: {
                        user.setDuelRequests(!user.isDuelRequests());
                        inventoryClickEvent.getInventory().setItem(12, new ItemBuilder(Material.IRON_SWORD).setName(ChatColor.YELLOW + "Duel Requests")
                                .setLore(null, ChatColor.AQUA + "Your dual requests are currently: " + (user.isDuelRequests() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled")).toItemStack());
                        break;
                    }
                }
                break;
            }
            case "Duel Creator": {
                Duel duel = duelManager.getDuelCreator(player.getUniqueId());
                if (duel == null) {
                    player.closeInventory();
                    return;
                }
                switch (inventoryClickEvent.getCurrentItem().getType()) {
                    case CHEST: {
                        player.openInventory(inventoryUtil.kitSelection(duel));
                        break;
                    }
                    case PAPER: {
                        player.openInventory(inventoryUtil.numberOfMatches(duel));
                        break;
                    }
                    case WOOL: {
                        player.closeInventory();

                        User targetUser = userManager.getUser(duel.getTarget());
                        if (targetUser == null) {
                            player.sendMessage(arena.getPrefix() + "An error has occurred, duel request cancelled.");
                            return;
                        }

                        targetUser.addDuel(duel);

                        player.sendMessage(arena.getPrefix() + "Successfully sent a duel request to " + Bukkit.getPlayer(duel.getTarget()).getName());
                    }
                }
                break;
            }
            case "Kit": {
                Duel duel = duelManager.getDuelCreator(player.getUniqueId());
                if (duel == null) {
                    player.closeInventory();
                    return;
                }
                if (inventoryClickEvent.getCurrentItem().getType() != Material.WOOL)
                    duel.setKit(kitManager.getKit(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName()));

                player.openInventory(inventoryUtil.duelCreationInventory(duel));
                break;
            }
            case "Number of Matches": {
                Duel duel = duelManager.getDuelCreator(player.getUniqueId());
                if (duel == null) {
                    player.closeInventory();
                    return;
                }
                if (inventoryClickEvent.getCurrentItem().getType() != Material.WOOL) {
                    for (DuelType duelType : DuelType.values()) {
                        if (inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().contains(duelType.getName()))
                            duel.setDuelType(duelType);
                    }
                }
                player.openInventory(inventoryUtil.duelCreationInventory(duel));
                break;
            }
            case "Spectate Matches": {
                player.closeInventory();
                player.teleport(Bukkit.getPlayer(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "")));
                break;
            }
            case "Edit your kit": {
                player.closeInventory();
                kitManager.addKitEditor(player, kitManager.getKit(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName()), user);
                break;
            }
        }
    }

    @EventHandler
    public void onHealthChange(FoodLevelChangeEvent foodLevelChangeEvent) {
        if (!(foodLevelChangeEvent.getEntity() instanceof Player))
            return;

        Player player = (Player) foodLevelChangeEvent.getEntity();

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.MATCH) {
            foodLevelChangeEvent.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        if (!(entityDamageEvent.getEntity() instanceof Player))
            return;

        Player player = (Player) entityDamageEvent.getEntity();

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.MATCH) {
            if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID)
                player.teleport(Bukkit.getWorld("training").getSpawnLocation());


            entityDamageEvent.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent inventoryOpenEvent) {
        if (inventoryOpenEvent.getInventory().getType() == InventoryType.ANVIL)
            inventoryOpenEvent.setCancelled(true);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent inventoryCloseEvent) {
        if (inventoryCloseEvent.getInventory().getName().equals("§bDuel Creator"))
            duelManager.removeDuelCreator(inventoryCloseEvent.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent playerDropItemEvent) {
        Player player = playerDropItemEvent.getPlayer();

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return;

        if (user.getUserState() != UserState.MATCH) {
            playerDropItemEvent.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent playerChangedWorldEvent) {
        if (playerChangedWorldEvent.getFrom().getName().equals("practice"))
            hologramManager.sendDefaultHolograms(playerChangedWorldEvent.getPlayer());
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent playerItemConsumeEvent) {
        if (kitManager.isEditingKit(playerItemConsumeEvent.getPlayer().getUniqueId()))
            playerItemConsumeEvent.setCancelled(true);
    }

    @EventHandler
    public void onFire(ProjectileLaunchEvent projectileLaunchEvent) {
        if (projectileLaunchEvent.getEntity() instanceof EnderPearl) {
            EnderPearl enderPearl = (EnderPearl) projectileLaunchEvent.getEntity();
            if (!(enderPearl.getShooter() instanceof Player))
                return;

            Player shooter = (Player) enderPearl.getShooter();
            if (kitManager.isEditingKit(shooter.getUniqueId()))
                projectileLaunchEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent potionSplashEvent) {
        ThrownPotion thrownPotion = potionSplashEvent.getPotion();
        if (!(thrownPotion.getShooter() instanceof Player))
            return;

        Player shooter = (Player) thrownPotion.getShooter();

        if (kitManager.isEditingKit(shooter.getUniqueId()))
            potionSplashEvent.setCancelled(true);
    }

}
