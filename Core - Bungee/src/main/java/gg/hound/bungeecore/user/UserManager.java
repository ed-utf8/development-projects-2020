package gg.hound.bungeecore.user;


import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private final HashMap<UUID, BungeeCoreUser> uuidBungeeCoreUserHashMap;

    public UserManager() {
        this.uuidBungeeCoreUserHashMap = new HashMap<>();
    }

    public BungeeCoreUser createUser(long id, String username, UUID uuid) {
        uuidBungeeCoreUserHashMap.put(uuid, new BungeeCoreUser(id, uuid, username));
        return getUser(uuid);
    }

    public BungeeCoreUser getUser(UUID uuid) {
        return uuidBungeeCoreUserHashMap.get(uuid);
    }

    public void clearUser(UUID uuid) {
        uuidBungeeCoreUserHashMap.remove(uuid);
    }

}
