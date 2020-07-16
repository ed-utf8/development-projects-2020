package gg.hound.bungeecore;

import gg.hound.bungeecore.announcements.AutoAnnouncer;
import gg.hound.bungeecore.commands.*;
import gg.hound.bungeecore.listeners.*;
import gg.hound.bungeecore.maintainence.MaintenanceMode;
import gg.hound.bungeecore.motd.MessageOfTheDay;
import gg.hound.bungeecore.redis.RedisManager;
import gg.hound.bungeecore.servermanager.PrivateServerManager;
import gg.hound.bungeecore.servermanager.ServerManager;
import gg.hound.bungeecore.sql.SQLManager;
import gg.hound.bungeecore.user.UserManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class BungeeCorePlugin extends Plugin {

    private File configFile;
    private Configuration configuration;

    private RedisManager redisManager;
    private SQLManager sqlManager;

    @Override
    public void onEnable() {

        configFile = new File(ProxyServer.getInstance().getPluginsFolder().getPath() + "/BungeeCorePlugin", "config.yml");

        try {

            if (!configFile.exists()) {
                new File(ProxyServer.getInstance().getPluginsFolder(), "BungeeCorePlugin").mkdirs();
                configFile.createNewFile();

                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

                configuration.set("host", "127.0.0.1");
                configuration.set("port", 3306);
                configuration.set("database", "db1");
                configuration.set("username", "username");
                configuration.set("password", "password");
                configuration.set("redis-host", "127.0.0.1");
                configuration.set("redis-port", 6397);
                configuration.set("redis-pass", "password");
                configuration.set("vpn.key", "apiKeyHere");

                configuration.set("maintenance", false);

                configuration.set("motd.line1", "&c&lHound Network &7-&e Welcome to Hound!");
                configuration.set("motd.line2", "&cFollow us on Twitter &7-&e @Hound_GG");

                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ThreadFactory factory = ((ThreadPoolExecutor) getExecutorService()).getThreadFactory();
        getExecutorService().shutdownNow();


        try {
            Field field = Plugin.class.getDeclaredField("service");
            field.setAccessible(true);
            ScheduledExecutorService service;
            field.set(this, service = Executors.newScheduledThreadPool(48, factory));
            System.out.println("[BungeeCorePlugin] Overridden BungeeCord Service.");
        } catch (Exception e) {
            throw new RuntimeException("Can't replace BungeeCord thread pool with our own", e);
        }

        this.redisManager = new RedisManager(this);
        this.redisManager.onEnable();

        UserManager userManager = new UserManager();
        this.sqlManager = new SQLManager(this, configuration, userManager);

        ServerManager serverManager = new ServerManager(this, redisManager, sqlManager);

        MessageOfTheDay messageOfTheDay = new MessageOfTheDay(this);
        messageOfTheDay.loadMoTD(configuration);

        MaintenanceMode maintenanceMode = new MaintenanceMode(this);
        maintenanceMode.loadMaintainance(configuration);

        PrivateServerManager privateServerManager = new PrivateServerManager(redisManager);

        new AutoAnnouncer(this);

        register(new ProxyPingListener(maintenanceMode, messageOfTheDay));
        register(new PostLoginListener(this, userManager, sqlManager, maintenanceMode, serverManager));
        register(new ServerCreateListener(this));
        register(new ServerDeleteListener(this, serverManager));
        register(new ServerKickListener(this, serverManager));

        register(new MaintenanceModeCommand(this, maintenanceMode));
        register(new MoTDCommand(this, messageOfTheDay));
        register(new ServerManagerCommand(this, serverManager));
        register(new HubCommand(this, serverManager));
        register(new JoinMeCommand(this, redisManager));
        register(new RedisPingCommand(this, redisManager));
        register(new PrivateServerCommand(this, privateServerManager));
        register(new JoinServerCommand(this));

    }

    @Override
    public void onDisable() {
        if (this.redisManager != null) this.redisManager.onDisable();
        if (this.sqlManager != null) this.sqlManager.close();
    }

    public void log(String message) {
        getProxy().getLogger().info("[BungeeCorePlugin] " + message);
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        return configuration;
    }

    public String getStaffPrefix() {
        return "§c§lStaff §8\u00bb §f";
    }

    public String getServerManagerPrefix() {
        return "§c§lDeployment §7\u00bb §f";
    }

    public String getPrefix() {
        return "§c§lHound §7\u00bb §e";
    }

    private void register(Listener listener) {
        ProxyServer.getInstance().getPluginManager().registerListener(this, listener);
    }

    private void register(Command command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, command);
    }
}
