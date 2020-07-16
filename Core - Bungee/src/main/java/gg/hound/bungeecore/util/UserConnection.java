package gg.hound.bungeecore.util;

public class UserConnection {

    private final String userAddress;
    private final long ipId;
    private final boolean vpn;

    public UserConnection(String userAddress, long ipId, boolean vpn) {
        this.userAddress = userAddress;
        this.ipId = ipId;
        this.vpn = vpn;
    }

    public boolean isVpn() {
        return vpn;
    }

    public long getIpId() {
        return ipId;
    }

    public String getUserAddress() {
        return userAddress;
    }
}
