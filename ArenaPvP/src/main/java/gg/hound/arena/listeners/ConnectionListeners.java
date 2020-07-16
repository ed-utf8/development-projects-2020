package gg.hound.arena.listeners;

import gg.hound.arena.Arena;
import gg.hound.arena.hologram.Hologram;
import gg.hound.arena.hologram.HologramManager;
import gg.hound.arena.match.Match;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.scoreboard.ScoreboardManager;
import gg.hound.arena.scoreboard.packet.PacketInterceptor;
import gg.hound.arena.scoreboard.packet.PacketListener;
import gg.hound.arena.sql.SQLManager;
import gg.hound.arena.user.SpectatorManager;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.util.InventoryUtil;
import gg.hound.arena.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConnectionListeners implements Listener {

    private final Arena arena;
    private final PacketUtil packetUtil;
    private final InventoryUtil inventoryUtil;
    private final SQLManager sqlManager;
    private final UserManager userManager;
    private final PacketListener packetListener;
    private final ScoreboardManager scoreboardManager;
    private final MatchManager matchManager;
    private final HologramManager hologramManager;
    private final SpectatorManager spectatorManager;
    private final KitManager kitManager;

    private final Executor INJECT_EXECUTOR = Executors.newSingleThreadExecutor();
    private final Executor EJECT_EXECUTOR = Executors.newSingleThreadExecutor();
    private final Map<UUID, PacketInterceptor> packetInterceptorMap;

    public ConnectionListeners(Arena arena, PacketUtil packetUtil, InventoryUtil inventoryUtil, SQLManager sqlManager, UserManager userManager, PacketListener packetListener, ScoreboardManager scoreboardManager, MatchManager matchManager, HologramManager hologramManager, SpectatorManager spectatorManager, KitManager kitManager) {
        this.arena = arena;
        this.packetUtil = packetUtil;
        this.inventoryUtil = inventoryUtil;
        this.sqlManager = sqlManager;
        this.userManager = userManager;
        this.packetListener = packetListener;
        this.scoreboardManager = scoreboardManager;
        this.matchManager = matchManager;
        this.hologramManager = hologramManager;
        this.spectatorManager = spectatorManager;
        this.kitManager = kitManager;

        packetInterceptorMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
        if (asyncPlayerPreLoginEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        if (!arena.canWeConnect()) {
            asyncPlayerPreLoginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Server not ready");
            return;
        }


        User user = sqlManager.loadUser(asyncPlayerPreLoginEvent.getUniqueId());
        if (user == null) {
            asyncPlayerPreLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            asyncPlayerPreLoginEvent.setKickMessage("Error loading user from database");
            return;
        }

        Map<Kit, ItemStack[]> userKits = sqlManager.userKits(user.getId());
        user.setInventoryLayout(userKits == null ? new HashMap<>() : userKits);


        userManager.addUser(asyncPlayerPreLoginEvent.getUniqueId(), user);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);

        player.setHealth(20.0);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setFireTicks(0);

        INJECT_EXECUTOR.execute(() -> {
            PacketInterceptor packetInterceptor = new PacketInterceptor(player, packetListener);
            packetInterceptor.attach();
            packetInterceptorMap.put(player.getUniqueId(), packetInterceptor);
        });

        scoreboardManager.setScoreboard(player);

        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());

        packetUtil.sendActionBar(player, ChatColor.RED + "Welcome " + ChatColor.YELLOW + player.getName());

        packetUtil.sendHeaderFooter(playerJoinEvent.getPlayer(),
                new StringJoiner("\n")
                        .add("")
                        .add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "HOUND NETWORK " + ChatColor.GRAY + ChatColor.BOLD + "\u007c " + ChatColor.WHITE + ChatColor.BOLD + "EU.HOUND.GG")
                        .add("")
                        .toString(),
                new StringJoiner("\n")
                        .add("")
                        .add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "  Website: " + ChatColor.WHITE + "https://hound.gg")
                        .add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "  Store: " + ChatColor.WHITE + "https://store.hound.gg")
                        .add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "  Discord: " + ChatColor.WHITE + "https://hound.gg/discord")
                        .add("")
                        .toString());

        inventoryUtil.giveSpawnInventory(player);

        player.teleport(Bukkit.getWorld("training").getSpawnLocation());

        for (UUID spectators : spectatorManager.getSpectators())
            player.hidePlayer(Bukkit.getPlayer(spectators));

        hologramManager.sendDefaultHolograms(player);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();

        EJECT_EXECUTOR.execute(() -> {
            if (packetInterceptorMap.containsKey(player.getUniqueId())) {
                PacketInterceptor interceptor = packetInterceptorMap.get(player.getUniqueId());
                if (interceptor.isAttached())
                    interceptor.detach();
                packetInterceptorMap.remove(player.getUniqueId());
            }
        });

        matchManager.removeFromQueue(player.getUniqueId());

        if (matchManager.getMatch(player.getUniqueId()) != null) {
            Match match = matchManager.getMatch(player.getUniqueId());
            if (match == null)
                return;

            if (match.isRandomizer())
                matchManager.handleRandomizerMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
            else
                matchManager.handleSoloMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
        }

        spectatorManager.removeSpectator(player, userManager.getUser(player.getUniqueId()));

        sqlManager.saveMatches(userManager.getUser(player.getUniqueId()));

        userManager.removeUser(player.getUniqueId());

        kitManager.removeKitEditor(player);
    }

    @EventHandler
    public void onKick(PlayerKickEvent playerKickEvent) {
        Player player = playerKickEvent.getPlayer();

        EJECT_EXECUTOR.execute(() -> {
            if (packetInterceptorMap.containsKey(player.getUniqueId())) {
                PacketInterceptor interceptor = packetInterceptorMap.get(player.getUniqueId());
                if (interceptor.isAttached())
                    interceptor.detach();
                packetInterceptorMap.remove(player.getUniqueId());
            }
        });

        matchManager.removeFromQueue(player.getUniqueId());

        if (matchManager.getMatch(player.getUniqueId()) != null) {
            Match match = matchManager.getMatch(player.getUniqueId());
            if (match == null)
                return;

            if (match.isRandomizer())
                matchManager.handleRandomizerMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
            else
                matchManager.handleSoloMatchEnd(match, match.getPlayerOne() == player.getUniqueId() ? match.getPlayerTwo() : match.getPlayerOne());
        }

        kitManager.removeKitEditor(player);
        spectatorManager.removeSpectator(player, userManager.getUser(player.getUniqueId()));

        sqlManager.saveMatches(userManager.getUser(player.getUniqueId()));

        userManager.removeUser(player.getUniqueId());

    }

}
