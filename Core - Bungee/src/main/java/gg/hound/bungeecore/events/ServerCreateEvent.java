package gg.hound.bungeecore.events;

import net.md_5.bungee.api.plugin.Event;

public class ServerCreateEvent extends Event {

    private final String serverName, serverAddress;
    private final int serverPort;

    public ServerCreateEvent(String serverName, String serverAddress, int serverPort) {
        this.serverName = serverName;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

}
