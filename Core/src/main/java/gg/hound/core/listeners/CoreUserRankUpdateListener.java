package gg.hound.core.listeners;

import gg.hound.core.CorePlugin;
import gg.hound.core.events.CoreUserRankUpdateEvent;
import gg.hound.core.group.Group;
import gg.hound.core.group.GroupManager;
import gg.hound.core.group.PermissionsHandler;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CoreUserRankUpdateListener implements Listener {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final PermissionsHandler permissionsHandler;
    private final GroupManager groupManager;

    public CoreUserRankUpdateListener(CorePlugin corePlugin, UserManager userManager, PermissionsHandler permissionsHandler, GroupManager groupManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.permissionsHandler = permissionsHandler;
        this.groupManager = groupManager;
    }

    @EventHandler
    public void onPermissionUserRankUpdate(CoreUserRankUpdateEvent coreUserRankUpdateEvent) {

        if (coreUserRankUpdateEvent.getGroupAction() == CoreUserRankUpdateEvent.GroupAction.ADD) {

            if (groupManager.groupExists(coreUserRankUpdateEvent.getGroup())) return;

            Group permissionGroup = groupManager.getGroup(coreUserRankUpdateEvent.getGroup());

            if (Bukkit.getOfflinePlayer(coreUserRankUpdateEvent.getUuid()).isOnline()) {
                Player player = Bukkit.getPlayer(coreUserRankUpdateEvent.getUuid());
                CoreUser permissionUser = userManager.getUser(coreUserRankUpdateEvent.getUuid());
                permissionUser.addGroup(permissionGroup);
                permissionsHandler.addPermissions(player, permissionUser);
                player.sendMessage(corePlugin.getPrefix() + "Your permissions have been updated.");
                return;
            }
        }

        if (coreUserRankUpdateEvent.getGroupAction() == CoreUserRankUpdateEvent.GroupAction.REMOVE) {

            if (groupManager.groupExists(coreUserRankUpdateEvent.getGroup())) return;

            Group permissionGroup = groupManager.getGroup(coreUserRankUpdateEvent.getGroup());

            if (Bukkit.getOfflinePlayer(coreUserRankUpdateEvent.getUuid()).isOnline()) {
                Player player = Bukkit.getPlayer(coreUserRankUpdateEvent.getUuid());
                CoreUser permissionUser = userManager.getUser(coreUserRankUpdateEvent.getUuid());
                permissionUser.removeGroup(permissionGroup);
                permissionUser.updatePrefix();
                permissionsHandler.addPermissions(player, permissionUser);
                player.sendMessage(corePlugin.getPrefix() + "Your permissions have been updated.");
            }
        }


    }
}
