package gg.hound.core.punishments;

public class IPBan {

    private Reason reason;

    private final long time;
    private final boolean updateIp, banAltAccount;
    private final long punishmentId;

    public IPBan(Reason reason, long time, boolean updateIp, boolean banAltAccount, long punishmentId) {
        this.reason = reason;
        this.time = time;
        this.updateIp = updateIp;
        this.banAltAccount = banAltAccount;
        this.punishmentId = punishmentId;
    }

    public long getTime() {
        return time;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public boolean isUpdateIp() {
        return updateIp;
    }

    public boolean isBanAltAccount() {
        return banAltAccount;
    }

    public long getPunishmentId() {
        return punishmentId;
    }
}
