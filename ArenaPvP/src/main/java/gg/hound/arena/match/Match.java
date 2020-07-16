package gg.hound.arena.match;

import gg.hound.arena.match.kit.Kit;

import java.util.List;
import java.util.UUID;

public class Match {

    private final UUID playerOne;
    private final UUID playerTwo;

    private final String playerOneName;
    private final String playerTwoName;

    private int matchCountdown = 5;

    private final boolean ranked;
    private final boolean randomizer;

    private final Kit kit;

    public Match(UUID playerOne, UUID playerTwo, String playerOneName, String playerTwoName, boolean ranked, boolean randomizer) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.ranked = ranked;
        this.randomizer = randomizer;
        kit = null;
    }

    public Match(UUID playerOne, UUID playerTwo, String playerOneName, String playerTwoName, boolean ranked, boolean randomizer, Kit kit) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.ranked = ranked;
        this.randomizer = randomizer;
        this.kit = kit;
    }

    public UUID getPlayerOne() {
        return playerOne;
    }

    public UUID getPlayerTwo() {
        return playerTwo;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public boolean isRanked() {
        return ranked;
    }

    public boolean isRandomizer() {
        return randomizer;
    }

    public int getMatchCountdown() {
        return matchCountdown;
    }

    public void decrementMatchCountdown() {
        matchCountdown--;
    }

    public Kit getKit() {
        return kit;
    }
}
