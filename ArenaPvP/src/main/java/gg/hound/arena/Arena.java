package gg.hound.arena;

import gg.hound.arena.arenas.MapManager;
import gg.hound.arena.commands.SaveCommand;
import gg.hound.arena.commands.SpecCommand;
import gg.hound.arena.hologram.HologramManager;
import gg.hound.arena.hologram.adaptor.HologramTextAdaptor;
import gg.hound.arena.listeners.*;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.match.duel.DuelManager;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.scoreboard.ScoreboardManager;
import gg.hound.arena.scoreboard.packet.PacketListener;
import gg.hound.arena.scoreboard.packet.adaptors.ScoreboardTeamPacketAdaptor;
import gg.hound.arena.sql.SQLManager;
import gg.hound.arena.tasks.ArenaCreationTask;
import gg.hound.arena.tasks.MatchTeleportTask;
import gg.hound.arena.tasks.MatchTimerTask;
import gg.hound.arena.tasks.PartyTimerTask;
import gg.hound.arena.user.SpectatorManager;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.util.InventoryUtil;
import gg.hound.arena.util.PacketUtil;
import gg.hound.arena.world.WorldManager;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Arena extends JavaPlugin {

    private SQLManager sqlManager;
    private MatchTeleportTask matchTeleportTask;

    private boolean matchesEnabled = true;
    private boolean canConnect = false;

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
            log("[ERROR] Plugin failed to load as there was no config setup.");
            log("[ERROR] We have created a default config file for you.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new WorldManager(this);

        KitManager kitManager = new KitManager(this);
        sqlManager = new SQLManager(this, getConfig(), kitManager);

        ArenaCreationTask arenaCreationTask = new ArenaCreationTask(this);
        arenaCreationTask.runTaskTimer(this, 0L, 1L);

        MatchTimerTask matchTimerTask = new MatchTimerTask(this);
        matchTimerTask.runTaskTimer(this, 0L, 20L);

        PartyTimerTask partyTimerTask = new PartyTimerTask(this);
        partyTimerTask.runTaskTimer(this, 0L, 20L);

        MapManager mapManager = new MapManager(this, sqlManager, arenaCreationTask);
        PacketListener packetListener = new PacketListener();
        PacketUtil packetUtil = new PacketUtil();
        DuelManager duelManager = new DuelManager();
        UserManager userManager = new UserManager();
        MatchManager matchManager = new MatchManager(this, kitManager, mapManager, userManager, packetUtil, matchTimerTask, sqlManager, partyTimerTask);
        InventoryUtil inventoryUtil = new InventoryUtil(kitManager, matchManager);
        SpectatorManager spectatorManager = new SpectatorManager(this, inventoryUtil);
        ScoreboardManager scoreboardManager = new ScoreboardManager(this, matchManager);
        HologramManager hologramManager = new HologramManager();

        packetListener.registerAdapter(PacketPlayOutScoreboardTeam.class, new ScoreboardTeamPacketAdaptor(userManager));
        packetListener.registerAdapter(PacketPlayOutSpawnEntityLiving.class, new HologramTextAdaptor(userManager, kitManager));
        matchTeleportTask = new MatchTeleportTask(inventoryUtil, scoreboardManager, hologramManager);
        matchTeleportTask.runTaskTimer(this, 0L, 100L);


        Bukkit.getServer().getPluginManager().registerEvents(new ConnectionListeners(this, packetUtil, inventoryUtil, sqlManager, userManager, packetListener, scoreboardManager, matchManager, hologramManager, spectatorManager, kitManager), this);
        Bukkit.getServer().getPluginManager().registerEvents(new LobbyListeners(this, userManager, inventoryUtil, spectatorManager, kitManager, packetUtil, matchManager, duelManager, hologramManager), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ArenaListeners(userManager), this);
        Bukkit.getServer().getPluginManager().registerEvents(new WorldListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MatchListeners(this, matchManager, inventoryUtil, userManager), this);

        Bukkit.getPluginCommand("save").setExecutor(new SaveCommand(this, userManager, sqlManager, kitManager, inventoryUtil));
        Bukkit.getPluginCommand("spec").setExecutor(new SpecCommand(this, userManager));
    }

    @Override
    public void onDisable() {
        if (sqlManager != null)
            sqlManager.onDisable();
    }

    public boolean areMatchesEnabled() {
        return matchesEnabled;
    }

    public void setMatchesEnabled(boolean matchesEnabled) {
        this.matchesEnabled = matchesEnabled;
    }

    public void log(String message) {
        System.out.println("[Arena] " + message);
    }

    public String getPrefix() {
        return "§c§lHound §8\u00bb §e";
    }

    public void addForTeleport(Player player) {
        matchTeleportTask.teleportToSpawn(player);
    }

    public boolean canWeConnect() {
        return canConnect;
    }

    public void setCanConnect(boolean canConnect) {
        this.canConnect = canConnect;
    }
}
