package gg.hound.core.group;

import gg.hound.core.CorePlugin;
import gg.hound.core.user.CoreUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Map;

public class PermissionsHandler {

    private final CorePlugin corePlugin;

    public PermissionsHandler(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    public void addPermissions(Player player, CoreUser coreUser) {
        removePermissions(coreUser);

        PermissionAttachment permissionAttachment = player.addAttachment(corePlugin);

        for (Group permissionGroup : coreUser.getPermissionGroups()) {
            for (Map.Entry<String, Boolean> stringBooleanHashMap : permissionGroup.getStringBooleanHashMap().entrySet())
                permissionAttachment.setPermission(stringBooleanHashMap.getKey(), stringBooleanHashMap.getValue());
            permissionAttachment.setPermission("perms." + permissionGroup.getRankName(), true);
            permissionAttachment.setPermission("rank." + permissionGroup.getRankName(), true);
            permissionAttachment.setPermission("core." + permissionGroup.getRankName(), true);
        }

    }

    public void removePermissions(CoreUser coreUser) {
        Player player = Bukkit.getPlayer(coreUser.getUuid());

        if (player == null) return;

        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            PermissionAttachment attachment = permissionInfo.getAttachment();
            if (attachment != null) {
                Map<String, Boolean> flags = attachment.getPermissions();
                for (String permissions : flags.keySet())
                    attachment.unsetPermission(permissions);
            }
            player.getEffectivePermissions().clear();
        }

    }
}
