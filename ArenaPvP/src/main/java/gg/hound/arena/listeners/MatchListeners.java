package gg.hound.arena.listeners;

import gg.hound.arena.Arena;
import gg.hound.arena.match.Match;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.match.party.PartyMatch;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.user.UserState;
import gg.hound.arena.user.party.Party;
import gg.hound.arena.util.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.text.DecimalFormat;

public class MatchListeners implements Listener {

    private final Arena arena;
    private final MatchManager matchManager;
    private final InventoryUtil inventoryUtil;
    private final UserManager userManager;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public MatchListeners(Arena arena, MatchManager matchManager, InventoryUtil inventoryUtil, UserManager userManager) {
        this.arena = arena;
        this.matchManager = matchManager;
        this.inventoryUtil = inventoryUtil;
        this.userManager = userManager;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (!(entityDamageByEntityEvent.getEntity() instanceof Player))
            return;

        Player player = (Player) entityDamageByEntityEvent.getEntity();

        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
            Player damager = (Player) entityDamageByEntityEvent.getDamager();

            User damageUser = userManager.getUser(damager.getUniqueId());
            if (damageUser == null)
                return;

            if (damageUser.getUserState() == UserState.SPECTATOR) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }
        }

        if (entityDamageByEntityEvent.getFinalDamage() >= player.getHealth()) {
            Match match = matchManager.getMatch(player.getUniqueId());
            if (match == null) {
                User user = userManager.getUser(player.getUniqueId());
                if (user == null)
                    return;

                Party party = user.getParty();
                if (party == null)
                    return;

                PartyMatch partyMatch = matchManager.getMatch(party);
                if (partyMatch == null)
                    return;

                //TODO: Check for players alive, add to spectator when dead.
                return;
            }

            entityDamageByEntityEvent.setCancelled(true);
            if (match.isRandomizer())
                matchManager.handleRandomizerMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
            else
                matchManager.handleSoloMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
            return;
        }

        if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();
            if (!(arrow.getShooter() instanceof Player))
                return;

            Player shooter = (Player) arrow.getShooter();

            shooter.sendMessage(arena.getPrefix() + player.getName() + " has " + ChatColor.RED + decimalFormat.format(player.getHealth()) + ChatColor.YELLOW + " hearts");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (!(entityDamageEvent.getEntity() instanceof Player))
            return;

        Player player = (Player) entityDamageEvent.getEntity();

        if (entityDamageEvent.getFinalDamage() >= player.getHealth()) {
            Match match = matchManager.getMatch(player.getUniqueId());
            if (match == null)
                return;

            entityDamageEvent.setCancelled(true);
            if (match.isRandomizer())
                matchManager.handleRandomizerMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
            else
                matchManager.handleSoloMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent playerRespawnEvent) {
        inventoryUtil.giveSpawnInventory(playerRespawnEvent.getPlayer());
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent playerItemConsumeEvent) {
        Player player = playerItemConsumeEvent.getPlayer();

        Match match = matchManager.getMatch(player.getUniqueId());
        if (match == null)
            return;

        if (playerItemConsumeEvent.getItem().getType() != Material.GOLDEN_APPLE)
            return;

        if (playerItemConsumeEvent.getItem().getItemMeta().getDisplayName() == null)
            return;

        if (!playerItemConsumeEvent.getItem().getItemMeta().getDisplayName().equals("§6Golden Head"))
            return;

        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent entityRegainHealthEvent) {
        if (!(entityRegainHealthEvent.getEntity() instanceof  Player))
            return;

        Player player = (Player) entityRegainHealthEvent.getEntity();

        if (!matchManager.getNoHealthRegen().contains(player.getUniqueId()))
            return;

        if (entityRegainHealthEvent.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED)
            return;

        entityRegainHealthEvent.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent foodLevelChangeEvent) {
        if (!(foodLevelChangeEvent.getEntity() instanceof Player))
            return;

        Player player = (Player) foodLevelChangeEvent.getEntity();

        if (!matchManager.getNoHunger().contains(player.getUniqueId()))
            return;

        foodLevelChangeEvent.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();

        if (matchManager.getNoBuild().contains(player.getUniqueId()))
            blockPlaceEvent.setCancelled(true);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();

        if (matchManager.getNoBuild().contains(player.getUniqueId()))
            blockBreakEvent.setCancelled(true);
    }

}
