package gg.hound.arena.listeners;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListeners implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent playerPortalEvent) {
        playerPortalEvent.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent weatherChangeEvent) {
        weatherChangeEvent.setCancelled(weatherChangeEvent.toWeatherState());
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent leavesDecayEvent) {
        leavesDecayEvent.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent thunderChangeEvent) {
        thunderChangeEvent.setCancelled(thunderChangeEvent.toThunderState());
    }

    @EventHandler
    public void onPaintingBreak(HangingBreakEvent hangingBreakEvent) {
        hangingBreakEvent.setCancelled(true);
    }

    @EventHandler
    public void onPaintingBreakByEntity(HangingBreakByEntityEvent hangingBreakByEntityEvent) {
        hangingBreakByEntityEvent.setCancelled(true);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent explosionPrimeEvent) {
        explosionPrimeEvent.setCancelled(true);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent blockBurnEvent) {
        blockBurnEvent.setCancelled(true);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent blockSpreadEvent) {
        blockSpreadEvent.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent blockFadeEvent) {
        if (blockFadeEvent.getBlock().getType() == Material.FIRE)
            blockFadeEvent.setCancelled(true);
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent playerAchievementAwardedEvent) {
        playerAchievementAwardedEvent.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent playerBedEnterEvent) {
        playerBedEnterEvent.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        if (playerBucketEmptyEvent.getPlayer().getWorld().getName().equals("training"))
            playerBucketEmptyEvent.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent playerBucketFillEvent) {
        if (playerBucketFillEvent.getPlayer().getWorld().getName().equals("training"))
            playerBucketFillEvent.setCancelled(true);
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent playerEggThrowEvent) {
        if (playerEggThrowEvent.getPlayer().getWorld().getName().equals("training"))
            playerEggThrowEvent.setHatching(false);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent entityExplodeEvent) {
        if (entityExplodeEvent.getEntity() instanceof TNTPrimed)
            entityExplodeEvent.setCancelled(true);
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent blockIgniteEvent) {
        if (blockIgniteEvent.getBlock().getType() == Material.TNT)
            blockIgniteEvent.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;

        if (!(entityDamageEvent.getEntity() instanceof Player))
            return;

        Player player = (Player) entityDamageEvent.getEntity();

        if (!player.getWorld().getName().equals("training"))
            return;

        entityDamageEvent.setCancelled(true);
        player.teleport(player.getWorld().getSpawnLocation());
    }



}