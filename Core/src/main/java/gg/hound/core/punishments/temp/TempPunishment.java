package gg.hound.core.punishments.temp;

public class TempPunishment {

    private final String punishmentType;
    private final String startTime;
    private final String endTime;
    private final String reason;
    private final String punisher;
    private final boolean isActive;

    public TempPunishment(String punishmentType, String startTime, String endTime, String reason, String punisher, boolean isActive) {
        this.punishmentType = punishmentType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.punisher = punisher;
        this.isActive = isActive;
    }

    public String getPunishmentType() {
        return punishmentType;
    }

    public String getPunisher() {
        return punisher;
    }

    public String getReason() {
        return reason;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public boolean isActive() {
        return isActive;
    }
}
