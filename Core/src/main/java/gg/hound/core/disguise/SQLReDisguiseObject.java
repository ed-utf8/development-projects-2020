package gg.hound.core.disguise;

public class SQLReDisguiseObject {

    private final String disguiseName;
    private final int skinId;

    public SQLReDisguiseObject(String disguiseName, int skinId) {
        this.disguiseName = disguiseName;
        this.skinId = skinId;
    }

    public String getDisguiseName() {
        return disguiseName;
    }

    public int getSkinId() {
        return skinId;
    }
}
