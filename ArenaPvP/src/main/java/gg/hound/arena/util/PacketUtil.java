package gg.hound.arena.util;

import gg.hound.arena.util.reflection.ObjectBoundReflections;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {

    public void sendActionBar(Player player, String message) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, (byte) 2);

        getPlayerConnection(player).sendPacket(packetPlayOutChat);
    }

    public void sendHeaderFooter(Player player, String header, String footer) {
        PacketPlayOutPlayerListHeaderFooter packetPlayOutPlayerListHeaderFooter = new ObjectBoundReflections(
                new PacketPlayOutPlayerListHeaderFooter())
                .set("a", serialize(header))
                .set("b", serialize(footer)).unbind();

        getPlayerConnection(player).sendPacket(packetPlayOutPlayerListHeaderFooter);
    }

    private IChatBaseComponent serialize(String text) {
        String JSON_TEXT = "{text: \"%s\"}";
        return IChatBaseComponent.ChatSerializer.a(String.format(JSON_TEXT, text));
    }


    private PlayerConnection getPlayerConnection(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection;
    }

}
