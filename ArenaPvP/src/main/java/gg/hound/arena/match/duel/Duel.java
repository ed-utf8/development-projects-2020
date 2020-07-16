package gg.hound.arena.match.duel;

import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.match.kit.KitManager;

import java.util.UUID;

public class Duel {

    private Kit kit;

    private final UUID sender;
    private final UUID target;

    private DuelType duelType;

    public Duel(UUID sender, UUID target, KitManager kitManager) {
        this.kit = kitManager.getKit("FinalUHC");
        this.sender = sender;
        this.target = target;
        this.duelType = DuelType.BEST_OF_ONE;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getTarget() {
        return target;
    }

    public DuelType getDuelType() {
        return duelType;
    }

    public void setDuelType(DuelType duelType) {
        this.duelType = duelType;
    }
}
