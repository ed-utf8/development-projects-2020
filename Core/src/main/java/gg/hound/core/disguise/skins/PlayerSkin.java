package gg.hound.core.disguise.skins;

import gg.hound.core.disguise.api.DisguiseObject;

public class PlayerSkin {

    private final int skinId;
    private final String skinValue;
    private final String skinSignature;

    public PlayerSkin(int skinId, String skinValue, String skinSignature) {
        this.skinId = skinId;
        this.skinValue = skinValue;
        this.skinSignature = skinSignature;
    }

    public int getSkinId() {
        return skinId;
    }

    public DisguiseObject getDisguiseObject(String name) {
        return new DisguiseObject(name, skinValue, skinSignature);
    }
}
