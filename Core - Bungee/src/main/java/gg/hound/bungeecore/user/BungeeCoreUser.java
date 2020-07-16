package gg.hound.bungeecore.user;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BungeeCoreUser {

    private final Long userId;
    private final UUID uuid;
    private final String name;
    private final List<String> groups;

    public BungeeCoreUser(Long userId, UUID uuid, String name) {
        this.userId = userId;
        this.uuid = uuid;
        this.name = name;

        groups = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setGroups(List<String> groups) {
        this.groups.addAll(groups);
    }

    public List<String> getGroups() {
        return groups;
    }

    public Long getUserId() {
        return userId;
    }
}
