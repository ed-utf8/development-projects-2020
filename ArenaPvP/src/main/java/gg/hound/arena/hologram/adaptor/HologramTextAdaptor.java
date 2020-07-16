package gg.hound.arena.hologram.adaptor;

import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.scoreboard.packet.PacketAdaptor;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.util.reflection.FieldBoundReflections;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HologramTextAdaptor implements PacketAdaptor<PacketPlayOutSpawnEntityLiving> {

    private final UserManager userManager;
    private final KitManager kitManager;
    private final FieldBoundReflections entityId, dataWatcher, entityStand;

    public HologramTextAdaptor(UserManager userManager, KitManager kitManager) {
        this.userManager = userManager;
        this.kitManager = kitManager;
        entityId = FieldBoundReflections.access(PacketPlayOutSpawnEntityLiving.class, "b");
        dataWatcher = FieldBoundReflections.access(PacketPlayOutSpawnEntityLiving.class, "l");
        entityStand = FieldBoundReflections.access(DataWatcher.class, "a");
    }

    @Override
    public Packet<?> handle(Player player, PacketPlayOutSpawnEntityLiving packet) {

        if (Bukkit.isPrimaryThread()){
            return packet;
        }

        int id = entityId.get(packet);

        if (id != 30)
            return packet;

        DataWatcher dataWatcher = this.dataWatcher.get(packet);

        EntityArmorStand entity = this.entityStand.get(dataWatcher);

        String entityName = entity.getCustomName();

        User user = userManager.getUser(player.getUniqueId());

        entity.setCustomName(
                entityName
                        .replaceAll("%name%", player.getName())
                        .replaceAll("%finaluhc%", "" + user.getElo(kitManager.getKit(1)))
                        .replaceAll("%nodebuff%", "" + user.getElo(kitManager.getKit(2)))
                        .replaceAll("%sg%", "" + user.getElo(kitManager.getKit(3)))
                        .replaceAll("%matchesremaining%", "" + (user.getMatchesRemaining() == -1 ? "Unlimited" : user.getMatchesRemaining()))
        );

        return packet;

    }
}

