package gg.hound.arena.tasks;

import gg.hound.arena.Arena;
import gg.hound.arena.arenas.block.BlockInfo;
import gg.hound.arena.match.Match;
import gg.hound.arena.match.party.PartyMatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchTimerTask extends BukkitRunnable {

    private final Arena arena;

    public List<Match> matches = new ArrayList<>();

    public MatchTimerTask(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        for (Match match : matches) {
            Player playerOne = Bukkit.getPlayer(match.getPlayerOne());
            Player playerTwo = Bukkit.getPlayer(match.getPlayerTwo());
            if (match.getMatchCountdown() == 0) {
                playerOne.sendMessage(arena.getPrefix() + "May the best player win!");
                playerOne.playSound(playerOne.getLocation(), Sound.WITHER_SPAWN, 1, 1);

                playerOne.removePotionEffect(PotionEffectType.JUMP);
                playerOne.removePotionEffect(PotionEffectType.SLOW);

                playerTwo.sendMessage(arena.getPrefix() + "May the best player win!");
                playerTwo.playSound(playerTwo.getLocation(), Sound.WITHER_SPAWN, 1, 1);

                playerTwo.removePotionEffect(PotionEffectType.JUMP);
                playerTwo.removePotionEffect(PotionEffectType.SLOW);

                matches.remove(match);
                return;
            }

            playerOne.sendMessage(arena.getPrefix() + "Match starting in " + ChatColor.RED + match.getMatchCountdown() + ChatColor.YELLOW + " seconds");
            playerOne.playSound(playerOne.getLocation(), Sound.ORB_PICKUP, 1, 1);

            playerTwo.sendMessage(arena.getPrefix() + "Match starting in " + ChatColor.RED + match.getMatchCountdown() + ChatColor.YELLOW + " seconds");
            playerTwo.playSound(playerTwo.getLocation(), Sound.ORB_PICKUP, 1, 1);

            match.decrementMatchCountdown();
        }
    }

    public void addMatch(Match match) {
        matches.add(match);
    }
}
