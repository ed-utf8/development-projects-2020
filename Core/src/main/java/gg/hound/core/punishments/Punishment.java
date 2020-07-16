package gg.hound.core.punishments;

import gg.hound.core.user.TempInfoStoreUser;

public class Punishment {

    private final TempInfoStoreUser target;
    private final TempInfoStoreUser executor;
    private Reason reason;
    private int time;
    private boolean customReason = false;
    private boolean customTime = false;

    public Punishment(TempInfoStoreUser target, TempInfoStoreUser executor, int time) {
        this.target = target;
        this.executor = executor;
        this.time = time;
    }

    public Punishment(TempInfoStoreUser target, TempInfoStoreUser executor) {
        this.target = target;
        this.executor = executor;
        this.time = 0;
    }

    public TempInfoStoreUser getTarget() {
        return target;
    }

    public TempInfoStoreUser getExecutor() {
        return executor;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public void setCustomReason(boolean customReason) {
        this.customReason = customReason;
    }

    public void setCustomTime(boolean customTime) {
        this.customTime = customTime;
    }

    public boolean isCustomReason() {
        return customReason;
    }

    public boolean isCustomTime() {
        return customTime;
    }
}
