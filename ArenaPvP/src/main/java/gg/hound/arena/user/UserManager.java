package gg.hound.arena.user;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class UserManager {

    private final Map<UUID, User> userMap = new WeakHashMap<>();

    public void addUser(UUID uuid, User user) {
        userMap.put(uuid, user);
    }

    public void removeUser(UUID uuid) {
        userMap.remove(uuid);
    }

    public Collection<User> getUsers() {
        return userMap.values();
    }

    public User getUser(UUID uuid) {
        return userMap.get(uuid);
    }

}
