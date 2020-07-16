package gg.hound.core.user;

import java.util.UUID;

public class TempInfoStoreUser {

    private final Long id;
    private final UUID uuid;
    private final String name;
    private final long ipId;
    private boolean disguise = false;

    public TempInfoStoreUser(Long id, UUID uuid, String name, long ipId) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.ipId = ipId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public boolean isDisguise() {
        return disguise;
    }

    public TempInfoStoreUser setDisguise(boolean disguise) {
        this.disguise = disguise;
        return this;
    }

    public long getIpId() {
        return ipId;
    }
}
