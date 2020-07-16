package gg.hound.core.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.group.GroupManager;
import gg.hound.core.sql.SQLManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;
    private final GroupManager groupManager;

    private final HashMap<UUID, CoreUser> coreUserHashMap;

    public UserManager(CorePlugin corePlugin, SQLManager sqlManager, GroupManager groupManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
        this.groupManager = groupManager;

        this.coreUserHashMap = new HashMap<>();
    }

    public CoreUser getUser(UUID uuid) {
        return coreUserHashMap.computeIfAbsent(uuid, user -> new CoreUser(uuid, groupManager));
    }

    public CoreUser getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public boolean userExists(UUID uuid) {
        return coreUserHashMap.containsKey(uuid);
    }

    public void removeUser(UUID uuid) {
        if (userExists(uuid))
            coreUserHashMap.remove(uuid);
    }
}
