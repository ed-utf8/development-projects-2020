package gg.hound.core.disguise.api;

public class DisguiseObject {

    private final String value;
    private final String signature;
    private String name;

    public DisguiseObject(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public String getSignature() {
        return this.signature;
    }
}
