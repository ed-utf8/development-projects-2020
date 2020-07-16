package gg.hound.arena.user.party;

import gg.hound.arena.match.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Party {

    private UUID partyLeader;

    private final List<UUID> partyMembers = new ArrayList<>();
    private final List<UUID> invitedMembers = new ArrayList<>();

    private Map<Kit, Integer> partyElo = new HashMap<>();

    public Party(UUID partyLeader) {
        this.partyLeader = partyLeader;
        partyMembers.add(partyLeader);
    }

    public UUID getPartyLeader() {
        return partyLeader;
    }

    public List<UUID> getPartyMembers() {
        return partyMembers;
    }

    public void addPartyMember(UUID uuid) {
        partyMembers.add(uuid);
    }

    public void removePartyMember(UUID uuid) {
        partyMembers.remove(uuid);

        if (partyMembers.size() == 1)
            return;

        if (uuid == partyLeader)
            reassignPartyLeader();

    }

    private void reassignPartyLeader() {
        partyLeader = partyMembers.get(0);
    }

    public int getElo(Kit kit) {
        return partyElo.get(kit);
    }

    public void setElo(Kit kit, int value) {
        partyElo.replace(kit, value);
    }

    public void setPartyElo(Map<Kit, Integer> partyElo) {
        this.partyElo = partyElo;
    }

    public void broadcastMessage(String message) {
        Player player;
        for (UUID uuid : partyMembers) {
            player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.sendMessage(message);
        }
    }

    public void broadcastMessage(String[] message) {
        Player player;
        for (UUID uuid : partyMembers) {
            player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            player.sendMessage(message);
        }
    }

    public List<UUID> getInvitedMembers() {
        return invitedMembers;
    }

    public void addInvite(UUID uuid) {
        invitedMembers.add(uuid);
    }

    public void removeInvite(UUID uuid) {
        invitedMembers.remove(uuid);
    }

    public void playSound(Sound sound, int volume, int pitch) {
        Player player;
        for (UUID uuid : partyMembers) {
            player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void removePotionEffect(PotionEffectType potionEffectType) {
        Player player;
        for (UUID uuid : partyMembers) {
            player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            player.removePotionEffect(potionEffectType);
        }
    }

    public void removeAllPotionEffects() {
        for (UUID uuid : partyMembers) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
    }
}
