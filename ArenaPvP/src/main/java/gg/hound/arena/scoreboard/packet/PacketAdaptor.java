package gg.hound.arena.scoreboard.packet;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface PacketAdaptor<E extends Packet<?>> {

    Packet<?> handle(Player player, E packet);

    default Packet<?> handleAny(Player player, Packet<?> packet) {
        return handle(player, (E) packet);
    }

}
