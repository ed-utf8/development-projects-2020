package gg.hound.core.punishments;

public class Ban {

    private Reason reason;
    private long time;

    public Ban(Reason reason, long time) {
        this.reason = reason;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

}
