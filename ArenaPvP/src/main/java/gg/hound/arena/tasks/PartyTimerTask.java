package gg.hound.arena.tasks;

import gg.hound.arena.Arena;
import gg.hound.arena.match.party.PartyMatch;
import gg.hound.arena.user.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PartyTimerTask extends BukkitRunnable {

    private final Arena arena;

    public List<PartyMatch> partyMatches = new ArrayList<>();

    public PartyTimerTask(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        for (PartyMatch partyMatch : partyMatches) {
            Party partyOne = partyMatch.getPartyOne();
            Party partyTwo = partyMatch.getPartyTwo();
            if (partyMatch.getMatchCountdown() == 0) {
                partyOne.broadcastMessage(arena.getPrefix() + "May the best player win!");
                partyOne.playSound(Sound.WITHER_SPAWN, 1, 1);

                partyOne.removePotionEffect(PotionEffectType.JUMP);
                partyOne.removePotionEffect(PotionEffectType.SLOW);

                partyTwo.broadcastMessage(arena.getPrefix() + "May the best player win!");
                partyTwo.playSound(Sound.WITHER_SPAWN, 1, 1);

                partyTwo.removePotionEffect(PotionEffectType.JUMP);
                partyTwo.removePotionEffect(PotionEffectType.SLOW);

                partyMatches.remove(partyMatch);
                return;
            }

            partyOne.broadcastMessage(arena.getPrefix() + "Match starting in " + ChatColor.RED + partyMatch.getMatchCountdown() + ChatColor.YELLOW + " seconds");
            partyOne.playSound(Sound.ORB_PICKUP, 1, 1);

            partyTwo.broadcastMessage(arena.getPrefix() + "Match starting in " + ChatColor.RED + partyMatch.getMatchCountdown() + ChatColor.YELLOW + " seconds");
            partyTwo.playSound(Sound.ORB_PICKUP, 1, 1);

            partyMatch.decrementMatchCountdown();
        }
    }

    public void addPartyMatch(PartyMatch partyMatch) {
        partyMatches.add(partyMatch);
    }
}
