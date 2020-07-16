package gg.hound.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class MuteEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final UUID target;
    private final String reason;
    private final long unmuteTime;

    public MuteEvent(UUID target, String reason, long unmuteTime) {
        this.target = target;
        this.reason = reason;
        this.unmuteTime = unmuteTime;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public String getReason() {
        return reason;
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
