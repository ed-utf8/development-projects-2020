package gg.hound.arena.scoreboard.scoreboards;

import gg.hound.arena.match.MatchManager;
import gg.hound.arena.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class LobbyScoreboard {

    private final MatchManager matchManager;
    private final Scoreboard scoreboard;

    private final String
            colourPrimary = ChatColor.WHITE.toString(),
            colourSecondary = ChatColor.RED.toString();


    public LobbyScoreboard(MatchManager matchManager) {
        this.matchManager = matchManager;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        create();
    }

    private void create() {
        Objective objective = scoreboard.registerNewObjective("LobbyScoreboard", "dummy");
        objective.setDisplayName("§e§lHound Arena");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team matchesRemaining = scoreboard.registerNewTeam("matchesRemaining");
        Team globalElo = scoreboard.registerNewTeam("globalElo");
        Team playersOnline = scoreboard.registerNewTeam("playersOnline");
        Team playersInQueue = scoreboard.registerNewTeam("playersInQueue");
        Team playersInMatch = scoreboard.registerNewTeam("playersInMatch");
        Team scoreboardDesignFooter = scoreboard.registerNewTeam("designFooter");

        matchesRemaining.setPrefix(colourPrimary + " Ranked ");
        matchesRemaining.addEntry("Matches§7: " + colourSecondary);
        matchesRemaining.setSuffix("%matches%");

        globalElo.setPrefix(colourPrimary + " Global ");
        globalElo.addEntry("ELO§7: " + colourSecondary);
        globalElo.setSuffix("%globalelo%");

        playersOnline.setPrefix(colourPrimary + " Players On");
        playersOnline.setSuffix("0");
        playersOnline.addEntry("line§7: " + colourSecondary);

        playersInQueue.setPrefix(colourPrimary + " In ");
        playersInQueue.addEntry("Queue§7: " + colourSecondary);
        playersInQueue.setSuffix("0");

        playersInMatch.setPrefix(colourPrimary + " In ");
        playersInMatch.addEntry("Match§7: " + colourSecondary);
        playersInMatch.setSuffix("0");

        scoreboardDesignFooter.setPrefix("§e");
        scoreboardDesignFooter.addEntry("hound.gg");

        objective.getScore("§1").setScore(8);
        objective.getScore("ELO§7: " + colourSecondary).setScore(7);
        objective.getScore("Matches§7: " + colourSecondary).setScore(6);
        objective.getScore("§2").setScore(5);
        objective.getScore("line§7: " + colourSecondary).setScore(4);
        objective.getScore("Queue§7: " + colourSecondary).setScore(3);
        objective.getScore("Match§7: " + colourSecondary).setScore(2);

        objective.getScore("§3").setScore(1);
        objective.getScore("hound.gg").setScore(-1);

    }

    public void update() {
        scoreboard.getTeam("playersOnline").setSuffix(Bukkit.getServer().getOnlinePlayers().size() + "/200");
        scoreboard.getTeam("playersInQueue").setSuffix(matchManager.getPlayersInQueue());
        scoreboard.getTeam("playersInMatch").setSuffix(matchManager.getPlayersInMatch());
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
