package gg.hound.arena.scoreboard;

import gg.hound.arena.Arena;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.scoreboard.scoreboards.LobbyScoreboard;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class ScoreboardManager {

    private final Arena arena;
    private final LobbyScoreboard lobbyScoreboard;

    public ScoreboardManager(Arena arena, MatchManager matchManager) {
        this.arena = arena;
        this.lobbyScoreboard = new LobbyScoreboard(matchManager);

        new BukkitRunnable() {
            @Override
            public void run() {
                lobbyScoreboard.update();
                for (Player player : Bukkit.getServer().getOnlinePlayers())
                    updateScoreboard(player);
            }
        }.runTaskTimer(arena, 0, 20);
    }

    public void setScoreboard(Player player) {
        player.setScoreboard(lobbyScoreboard.getScoreboard());
    }

    public void updateScoreboard(Player player) {
        sendUpdate((CraftScoreboard) lobbyScoreboard.getScoreboard(), "rankedMatches", player);
    }

    private void sendUpdate(CraftScoreboard scoreboard, String team, Player player) {

        if (Bukkit.isPrimaryThread()){
            new BukkitRunnable(){
                @Override
                public void run() {
                    sendUpdate(scoreboard, team, player);
                }
            }.runTaskAsynchronously(arena);
            return;
        }

        Scoreboard serverScoreboard = scoreboard.getHandle();
        ScoreboardTeam scoreboardTeam = serverScoreboard.getTeam(team);

        if (scoreboardTeam == null) {
            return;
        }

        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam(scoreboardTeam, 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutScoreboardTeam);
    }


}
