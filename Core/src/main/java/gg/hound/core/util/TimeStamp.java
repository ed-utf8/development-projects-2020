package gg.hound.core.util;

public enum TimeStamp {
    INSTANT("i", 0L), SECOND("s", 1L), MINUTE("m", 60L), HOUR("h", 3600L), DAY("d", 86400L), MONTH("n", 2592000L), PERMANENT("perm", 1000000000L), PERMANENT1("p", 1000000000L);


    private final String string;
    private final long multiplier;

    TimeStamp(String string, long multiplier) {
        this.string = string;
        this.multiplier = multiplier;
    }

    public long getMultiplier() {
        return this.multiplier;
    }

    public String getString() {
        return this.string;
    }

    TimeStamp getTimeStamp(String string) {
        for (TimeStamp timeStamps : TimeStamp.values()) {
            if (timeStamps.getString().equals(string)) {
                return timeStamps;
            }
        }
        return null;
    }

}
