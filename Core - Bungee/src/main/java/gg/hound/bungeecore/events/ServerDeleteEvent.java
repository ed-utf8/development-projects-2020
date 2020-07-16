package gg.hound.bungeecore.events;

import net.md_5.bungee.api.plugin.Event;

public class ServerDeleteEvent extends Event {

    private final String serverName;

    public ServerDeleteEvent(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }


}
