package gg.hound.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class UnmuteEvent extends Event {

    private final static HandlerList handlerList = new HandlerList();

    private final UUID target;

    private final long unmuteTime;

    public UnmuteEvent(UUID target, long unmuteTime) {
        this.target = target;
        this.unmuteTime = unmuteTime;
        System.out.println("Ran?");
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public UUID getTarget() {
        return target;
    }

    public long getUnmuteTime() {
        return unmuteTime;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
