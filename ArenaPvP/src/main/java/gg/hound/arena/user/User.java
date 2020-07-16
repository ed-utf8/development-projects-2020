package gg.hound.arena.user;

import gg.hound.arena.match.duel.Duel;
import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.user.party.Party;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class User {

    private final long id;

    private UserState userState;

    private boolean duelRequests;
    private boolean partyRequests;
    private boolean canUseGiftMatches;
    private long lastGiftMatchesTime;

    private int matchesRemaining;

    private final List<Duel> duels = new ArrayList<>();

    private final Map<Kit, Integer> eloMap;
    private Map<Kit, ItemStack[]> inventoryLayout;

    private Party party = null;

    private Queue<UUID> kitQueue = null;

    public User(long id, Collection<Kit> kits, int matchesRemaining) {
        this.id = id;
        this.userState = UserState.LOBBY;
        this.duelRequests = true;
        this.partyRequests = true;
        this.matchesRemaining = matchesRemaining;
        this.eloMap = new HashMap<>();
        for (Kit kit : kits)
            eloMap.put(kit, 1400);

        this.canUseGiftMatches = false;
        this.lastGiftMatchesTime = -1;

        this.inventoryLayout = new HashMap<>();
    }

    public User(long id, Map<Kit, Integer> eloMap, boolean duelRequests, boolean partyRequests, int matchesRemaining) {
        this.id = id;
        this.userState = UserState.LOBBY;
        this.eloMap = eloMap;
        this.duelRequests = duelRequests;
        this.partyRequests = partyRequests;
        this.matchesRemaining = matchesRemaining;
        this.canUseGiftMatches = false;
        this.lastGiftMatchesTime = -1;

        this.inventoryLayout = new HashMap<>();
    }

    public User(long id, Map<Kit, Integer> eloMap, boolean duelRequests, boolean partyRequests, int matchesRemaining, boolean canUseGiftMatches, long lastGiftMatchesTime) {
        this.id = id;
        this.userState = UserState.LOBBY;
        this.eloMap = eloMap;
        this.duelRequests = duelRequests;
        this.partyRequests = partyRequests;
        this.matchesRemaining = matchesRemaining;
        this.canUseGiftMatches = canUseGiftMatches;
        this.lastGiftMatchesTime = lastGiftMatchesTime;

        this.inventoryLayout = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public boolean isDuelRequests() {
        return duelRequests;
    }

    public void setDuelRequests(boolean duelRequests) {
        this.duelRequests = duelRequests;
    }

    public boolean isPartyRequests() {
        return partyRequests;
    }

    public void setPartyRequests(boolean partyRequests) {
        this.partyRequests = partyRequests;
    }

    public int getMatchesRemaining() {
        return matchesRemaining;
    }

    public void decrementMatchesRemaining() {
        matchesRemaining--;
    }

    public void setMatchesRemaining(int matchesRemaining) {
        this.matchesRemaining = matchesRemaining;
    }

    public List<Duel> getDuels() {
        return duels;
    }

    public void addDuel(Duel duel) {
        duels.removeIf(d -> d.getSender() == duel.getSender());
        duels.add(duel);
    }

    public Integer getElo(Kit kit) {
        return eloMap.getOrDefault(kit, -1);
    }

    public void setElo(Kit kit, int elo) {
        eloMap.replace(kit, elo);
    }

    public Integer getGlobalElo() {
        int sumOfElo = 0;
        for (Kit kit : eloMap.keySet())
            sumOfElo += eloMap.get(kit);

        return sumOfElo / eloMap.keySet().size();
    }

    public boolean canUseGiftMatches() {
        return canUseGiftMatches;
    }

    public long getLastGiftMatchesTime() {
        return lastGiftMatchesTime;
    }

    public void setCanUseGiftMatches(boolean canUseGiftMatches) {
        this.canUseGiftMatches = canUseGiftMatches;
    }

    public void setLastGiftMatchesTime(long lastGiftMatchesTime) {
        this.lastGiftMatchesTime = lastGiftMatchesTime;
    }

    public void setInventoryLayout(Map<Kit, ItemStack[]> inventoryLayout) {
        this.inventoryLayout = inventoryLayout;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Queue<UUID> getKitQueue() {
        return kitQueue;
    }

    public void setKitQueue(Queue<UUID> kitQueue) {
        this.kitQueue = kitQueue;
    }

    public ItemStack[] getInventoryLayout(Kit kit) {
        return inventoryLayout.get(kit);
    }

    public void setInventoryLayout(Kit kit, ItemStack[] itemStacks) {
        if (inventoryLayout.get(kit) == null)
            inventoryLayout.put(kit, itemStacks);
        else
            inventoryLayout.replace(kit, itemStacks);
    }
}
