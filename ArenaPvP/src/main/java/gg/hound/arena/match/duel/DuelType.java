package gg.hound.arena.match.duel;

public enum DuelType {
    BEST_OF_ONE("Best of One", 1, 1),
    BEST_OF_THREE("Best of One", 3, 2),
    BEST_OF_FIVE("Best of One", 5, 3);

    private final String name;
    private final int matches;
    private final int matchesToWin;

    DuelType(String name, int matches, int matchesToWin) {
        this.name = name;
        this.matches = matches;
        this.matchesToWin = matchesToWin;
    }

    public String getName() {
        return name;
    }

    public int getMatches() {
        return matches;
    }

    public int getMatchesToWin() {
        return matchesToWin;
    }
}
