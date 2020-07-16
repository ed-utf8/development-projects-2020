package gg.hound.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReportUserEvent extends Event {

    private final static HandlerList handlerList = new HandlerList();

    private final String userName, userServer, userReported, userReportedReason;

    public ReportUserEvent(String userName, String userServer, String userReported, String userReportedReason) {
        this.userName = userName;
        this.userServer = userServer;
        this.userReported = userReported;
        this.userReportedReason = userReportedReason;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserReported() {
        return userReported;
    }

    public String getUserReportedReason() {
        return userReportedReason;
    }

    public String getUserServer() {
        return userServer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
