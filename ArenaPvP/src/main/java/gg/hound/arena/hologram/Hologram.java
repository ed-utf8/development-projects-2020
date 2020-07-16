package gg.hound.arena.hologram;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram {
    private final List<EntityArmorStand> entitylist = new ArrayList<>();
    private final String[] text;
    private final Location location;
    private final double DISTANCE = 0.25D;
    int count;

    public Hologram(String[] text, Location location) {
        this.text = text;
        this.location = location;
        create();
    }

    private void create() {
        for (String Text : this.text) {
            EntityArmorStand entity = new EntityArmorStand(((CraftWorld) this.location.getWorld()).getHandle(), this.location.getX(), this.location.getY(), this.location.getZ());
            entity.setCustomName(Text);
            entity.setCustomNameVisible(true);
            entity.setInvisible(true);
            entity.setGravity(false);
            entitylist.add(entity);
            this.location.subtract(0, this.DISTANCE, 0);
            count++;
        }

        for (int i = 0; i < count; i++) {
            this.location.add(0, this.DISTANCE, 0);
        }
        this.count = 0;
    }

    public void sendToPlayer(Player player) {
        for (EntityArmorStand armor : entitylist) {
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armor);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }


}
