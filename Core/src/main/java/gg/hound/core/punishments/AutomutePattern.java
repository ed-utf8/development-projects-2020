package gg.hound.core.punishments;

import java.util.regex.Pattern;

public class AutomutePattern {

    private final int id;

    private final Pattern pattern;

    private final int lengthSeconds;

    public AutomutePattern(int id, Pattern pattern, int lengthSeconds) {
        this.id = id;
        this.pattern = pattern;
        this.lengthSeconds = lengthSeconds;
    }

    public int getId() {
        return id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getLengthSeconds() {
        return lengthSeconds;
    }
}
