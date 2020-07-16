package gg.hound.arena.scoreboard.packet.adaptors;

import gg.hound.arena.scoreboard.packet.PacketAdaptor;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.util.reflection.FieldBoundReflections;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ScoreboardTeamPacketAdaptor implements PacketAdaptor<PacketPlayOutScoreboardTeam> {

    private final FieldBoundReflections suffixField;
    private final UserManager userManager;

    public ScoreboardTeamPacketAdaptor(UserManager userManager){
        this.userManager = userManager;
        this.suffixField = FieldBoundReflections.access(PacketPlayOutScoreboardTeam.class, "d");
    }

    @Override
    public Packet<?> handle(Player player, PacketPlayOutScoreboardTeam packet) {

        if (Bukkit.isPrimaryThread()) {
            return packet;
        }

        String suffix = suffixField.get(packet);

        if ("%matches%".equals(suffix.toLowerCase()))
            suffixField.set(packet, getMatches(player.getUniqueId()));

        if ("%globalelo%".equals(suffix.toLowerCase()))
            suffixField.set(packet, globalElo(player.getUniqueId()));

        return packet;
    }

    private String getMatches(UUID uuid) {
        User user = userManager.getUser(uuid);
        if (user != null) {
            if (user.getMatchesRemaining() == -1)
                return "Unlimited";
            else
                return String.valueOf(user.getMatchesRemaining());
        }
        return "-2";
    }

    private String globalElo(UUID uuid) {
        User user = userManager.getUser(uuid);
        if (user == null)
            return "1400";

        return String.valueOf(user.getGlobalElo());
    }

}
