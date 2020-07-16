package gg.hound.arena.match.duel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelManager {

    private final Map<UUID, Duel> duelCreators = new HashMap<>();

    private final Map<UUID, Duel> duelRequests = new HashMap<>();

    public void addDuelCreator(UUID uuid, Duel duel) {
        duelCreators.put(uuid, duel);
    }

    public Duel getDuelCreator(UUID uuid) {
        return duelCreators.get(uuid);
    }

    public void removeDuelCreator(UUID uuid) {
        duelCreators.remove(uuid);
    }

    public void addDuelRequest(UUID uuid, Duel duel) {
        duelRequests.put(uuid, duel);
    }

    public Duel getDuelRequest(UUID uuid) {
        return duelRequests.get(uuid);
    }

    public void removeDuelRequest(UUID uuid) {
        duelRequests.remove(uuid);
    }

}
