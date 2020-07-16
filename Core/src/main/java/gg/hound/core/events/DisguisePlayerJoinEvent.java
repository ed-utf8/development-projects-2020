package gg.hound.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DisguisePlayerJoinEvent extends Event {

    private final static HandlerList handlerList = new HandlerList();

    private final String realName, disguiseName;

    public DisguisePlayerJoinEvent(String realName, String disguiseName) {
        this.realName = realName;
        this.disguiseName = disguiseName;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public String getRealName() {
        return realName;
    }

    public String getDisguiseName() {
        return disguiseName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
