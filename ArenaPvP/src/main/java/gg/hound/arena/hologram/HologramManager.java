package gg.hound.arena.hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramManager {

    public void sendDefaultHolograms(Player player) {
        Hologram welcomeHologram = new Hologram(new String[]{
                ChatColor.RED + "" + ChatColor.BOLD + "Welcome to ArenaPvP " + ChatColor.WHITE + "%name%",
                ChatColor.RED + "Final UHC: " + ChatColor.WHITE + "%finaluhc%",
                ChatColor.RED + "No Debuff: " + ChatColor.WHITE + "%nodebuff%",
                ChatColor.RED + "SG: " + ChatColor.WHITE + "%sg%",
                ChatColor.RED + "Matches Remaining: " + ChatColor.WHITE + "%matchesremaining%"
        }, new Location(Bukkit.getWorld("training"), -4, 20, 5));

        welcomeHologram.sendToPlayer(player);
    }
}
