package gg.hound.core.util;

public class Report {

    private final String target;
    private final String reporter;
    private String reason;


    public Report(String target, String reporter) {
        this.target = target;
        this.reporter = reporter;
    }

    public String getTarget() {
        return target;
    }

    public String getReporter() {
        return reporter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
