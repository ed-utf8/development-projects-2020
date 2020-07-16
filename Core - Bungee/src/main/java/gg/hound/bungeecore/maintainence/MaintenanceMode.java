package gg.hound.bungeecore.maintainence;

import gg.hound.bungeecore.BungeeCorePlugin;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.UUID;

public class MaintenanceMode {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final ArrayList<UUID> whitelist = new ArrayList<>();

    public MaintenanceMode(BungeeCorePlugin bungeeCorePlugin) {
        this.bungeeCorePlugin = bungeeCorePlugin;
    }

    private boolean maintenanceMode = false;
    private boolean adminOnly = true;

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(boolean maintainedMode) {
        this.maintenanceMode = maintainedMode;
        bungeeCorePlugin.getConfig().set("maintenance", maintainedMode);
        bungeeCorePlugin.saveConfig();
    }

    public void loadMaintainance(Configuration configuration) {
        maintenanceMode = configuration.getBoolean("maintenance");
    }

    public ArrayList<UUID> getWhitelist() {
        return whitelist;
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public void setAdminOnly(boolean adminOnly) {
        this.adminOnly = adminOnly;
    }
}
