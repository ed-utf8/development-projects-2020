package gg.hound.core;

import gg.hound.core.commands.CommandManager;
import gg.hound.core.disguise.DisguiseManager;
import gg.hound.core.group.GroupManager;
import gg.hound.core.group.PermissionsHandler;
import gg.hound.core.listeners.CoreUserRankUpdateListener;
import gg.hound.core.listeners.InventoryListener;
import gg.hound.core.listeners.PlayerChatListener;
import gg.hound.core.listeners.PlayerJoinListener;
import gg.hound.core.listeners.PlayerQuitListener;
import gg.hound.core.listeners.ReportUserListener;
import gg.hound.core.listeners.StaffChatListener;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.tasks.PlayerCooldownTask;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CorePlugin extends JavaPlugin {

    private String serverName = "NOT_SET";

    private RedisManager redisManager;
    private SQLManager sqlManager;

    private boolean chatMuted = false;

    @Override
    public void onEnable() {
        log("Plugin is attempting to enable.");

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
            log("[ERROR] Plugin failed to load as there was no config setup.");
            log("[ERROR] We have created a default config file for you.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        GroupManager groupManager = new GroupManager();
        PluginUtils pluginUtils = new PluginUtils();
        PunishmentData punishmentData = new PunishmentData(pluginUtils);

        this.redisManager = new RedisManager(this);
        this.redisManager.onEnable();
        this.sqlManager = new SQLManager(this, getConfig(), punishmentData, redisManager, groupManager, pluginUtils);

        UserManager userManager = new UserManager(this, sqlManager, groupManager);
        DisguiseManager disguiseManager = new DisguiseManager(this, sqlManager, userManager);
        PermissionsHandler permissionsHandler = new PermissionsHandler(this);

        PlayerCooldownTask playerCooldownTask = new PlayerCooldownTask();
        playerCooldownTask.runTaskTimerAsynchronously(this, 20, 20);

        register(new PlayerJoinListener(this, userManager, sqlManager, disguiseManager, permissionsHandler, pluginUtils));
        register(new StaffChatListener(userManager));
        register(new ReportUserListener(userManager));
        register(new PlayerChatListener(this, userManager, playerCooldownTask, sqlManager, redisManager, pluginUtils, punishmentData, groupManager));
        register(new InventoryListener(this, punishmentData, pluginUtils, sqlManager, redisManager, userManager));
        register(new CoreUserRankUpdateListener(this, userManager, permissionsHandler, groupManager));
        register(new PlayerQuitListener(userManager, punishmentData, pluginUtils));

        new CommandManager(this, userManager, disguiseManager, sqlManager, redisManager, playerCooldownTask, punishmentData, groupManager, pluginUtils);
    }

    @Override
    public void onDisable() {
        for (OfflinePlayer offlinePlayer : Bukkit.getOperators())
            offlinePlayer.setOp(false);

        if (sqlManager != null)
            this.sqlManager.close();
        if (redisManager != null)
            this.redisManager.onDisable();
    }

    public void log(String message) {
        Bukkit.getLogger().info("[CorePlugin] " + message);
    }

    private void register(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public boolean isChatMuted() {
        return chatMuted;
    }

    public void setChatMuted(boolean chatMuted) {
        this.chatMuted = chatMuted;
    }

    public String getPrefix() {
        return "§c§lHound §8\u00bb §e";
    }

    public String getNoPerms() {
        return getPrefix() + "§4Error: §cYou do not have permission to do this.";
    }

}
