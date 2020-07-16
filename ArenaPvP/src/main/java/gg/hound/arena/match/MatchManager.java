package gg.hound.arena.match;

import gg.hound.arena.Arena;
import gg.hound.arena.arenas.BaseArena;
import gg.hound.arena.arenas.MapManager;
import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.match.kit.Randomizer;
import gg.hound.arena.match.party.PartyMatch;
import gg.hound.arena.sql.SQLManager;
import gg.hound.arena.tasks.MatchTimerTask;
import gg.hound.arena.tasks.PartyTimerTask;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.user.UserState;
import gg.hound.arena.user.party.Party;
import gg.hound.arena.util.ItemBuilder;
import gg.hound.arena.util.PacketUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.*;

public class MatchManager {

    private final Arena arena;
    private final KitManager kitManager;
    private final MapManager mapManager;
    private final UserManager userManager;
    private final PacketUtil packetUtil;
    private final MatchTimerTask matchTimerTask;
    private final SQLManager sqlManager;
    private final PartyTimerTask partyTimerTask;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private final Map<Kit, Queue<UUID>> soloRankedQueue = new HashMap<>();
    private final Map<Kit, Queue<UUID>> soloUnrankedQueue = new HashMap<>();
    private final Map<Kit, Queue<Party>> partyRankedQueue = new HashMap<>();
    private final Map<Kit, Queue<Party>> partyUnrankedQueue = new HashMap<>();

    private final Map<UUID, Match> matchMap = new HashMap<>();
    private final Map<Party, PartyMatch> partyMatchMap = new HashMap<>();

    private final List<UUID> noHealthRegen = new ArrayList<>();
    private final List<UUID> noBuild = new ArrayList<>();
    private final List<UUID> noHunger = new ArrayList<>();

    private final Queue<UUID> randomizerQueue = new ArrayDeque<>();

    private final Randomizer randomizer = new Randomizer();

    public MatchManager(Arena arena, KitManager kitManager, MapManager mapManager, UserManager userManager, PacketUtil packetUtil, MatchTimerTask matchTimerTask, SQLManager sqlManager, PartyTimerTask partyTimerTask) {
        this.arena = arena;
        this.kitManager = kitManager;
        this.mapManager = mapManager;
        this.userManager = userManager;
        this.packetUtil = packetUtil;
        this.matchTimerTask = matchTimerTask;
        this.sqlManager = sqlManager;
        this.partyTimerTask = partyTimerTask;

        loadQueues();
    }

    private void loadQueues() {
        for (Kit kit : kitManager.getKits()) {
            soloRankedQueue.put(kit, new ArrayDeque<>());
            soloUnrankedQueue.put(kit, new ArrayDeque<>());
            partyRankedQueue.put(kit, new ArrayDeque<>());
            partyUnrankedQueue.put(kit, new ArrayDeque<>());
        }
    }

    public String getPlayersInQueue() {
        int queue = 0;
        queue += randomizerQueue.size();

        for (Kit kit : soloRankedQueue.keySet())
            queue += soloRankedQueue.get(kit).size();

        for (Kit kit : soloUnrankedQueue.keySet())
            queue += soloUnrankedQueue.get(kit).size();

        return String.valueOf(queue);
    }

    public String getPlayersInMatch() {
        return String.valueOf(matchMap.keySet().size());
    }

    public Queue<UUID> getSoloRankedQueue(Kit kit) {
        return soloRankedQueue.get(kit);
    }

    public Queue<UUID> getSoloUnrankedQueue(Kit kit) {
        return soloUnrankedQueue.get(kit);
    }

    public void addMatch(UUID uuid, Match match) {
        matchMap.put(uuid, match);
    }

    public void addMatch(Party party, PartyMatch partyMatch) {
        partyMatchMap.put(party, partyMatch);
    }

    public void removeMatch(Party party) {
        partyMatchMap.remove(party);
    }

    public PartyMatch getMatch(Party party) {
        return partyMatchMap.get(party);
    }

    public void removeMatch(UUID uuid) {
        matchMap.remove(uuid);
    }

    public Match getMatch(UUID uuid) {
        return matchMap.get(uuid);
    }

    public void handleSoloQueue(Player player, Kit kit, boolean ranked) {
        Queue<UUID> queue;
        if (ranked)
            queue = soloRankedQueue.get(kit);
        else
            queue = soloUnrankedQueue.get(kit);
        if (queue == null)
            return;

        if (queue.size() >= 1) {
            UUID opponent = queue.poll();

            Player opponentPlayer = Bukkit.getPlayer(opponent);
            if (opponentPlayer == null) {
                addToQueue(player, kit, ranked);
                return;
            }

            User opponentUser = userManager.getUser(opponent);
            if (opponentUser == null) {
                addToQueue(player, kit, ranked);
                return;
            }

            BaseArena baseArena = mapManager.getSoloMap(kit.isBuilding());
            if (baseArena == null) {
                removeFromQueue(opponent);
                packetUtil.sendActionBar(player, ChatColor.RED + "We are currently out of arenas.");
                packetUtil.sendActionBar(opponentPlayer, ChatColor.RED + "We are currently out of arenas.");
                return;
            }

            handleSoloMatchStart(player, opponentPlayer, kit, ranked, baseArena);
        } else addToQueue(player, kit, ranked);
    }

    public void handlePartyQueue(Party party, Kit kit, boolean ranked) {
        Queue<Party> queue;
        if (ranked)
            queue = partyRankedQueue.get(kit);
        else
            queue = partyUnrankedQueue.get(kit);

        if (queue == null)
            return;

        if (queue.size() >= 1) {
            Party opponentParty = queue.poll();

            Player opponentPlayer = Bukkit.getPlayer(opponentParty.getPartyLeader());
            if (opponentPlayer == null) {
                addToQueue(party, kit, ranked);
                return;
            }

            if (opponentParty.getPartyMembers().size() != 2) {
                addToQueue(party, kit, ranked);
                opponentParty.broadcastMessage(arena.getPrefix() + "Insufficient party members");
                return;
            }

            BaseArena baseArena = mapManager.getPartyMap(kit.isBuilding());
            if (baseArena == null) {
                addToQueue(party, kit, ranked);
                return;
            }

            handlePartyMatchStart(party, opponentParty, kit, ranked, baseArena);
        }
    }

    public void addToRandomizerQueue(Player player) {
        randomizerQueue.add(player.getUniqueId());

        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.WORKBENCH).setName(ChatColor.YELLOW + "Randomizer Queue").toItemStack());
        player.getInventory().setItem(8, new ItemBuilder(Material.REDSTONE_TORCH_ON).setName(ChatColor.YELLOW + "Exit Queue").toItemStack());
        player.updateInventory();
    }

    private void addToQueue(Player player, Kit kit, boolean ranked) {
        if (ranked)
            soloRankedQueue.get(kit).add(player.getUniqueId());
        else
            soloUnrankedQueue.get(kit).add(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setItem(0, kit.getInventoryItem());
        player.getInventory().setItem(8, new ItemBuilder(Material.REDSTONE_TORCH_ON).setName(ChatColor.YELLOW + "Exit Queue").toItemStack());
        player.updateInventory();
    }

    private void addToQueue(Party party, Kit kit, boolean ranked) {
        if (ranked)
            partyRankedQueue.get(kit).add(party);
        else
            partyUnrankedQueue.get(kit).add(party);

        //CLEAR INVENTORIES HERE
    }

    public void removeFromQueue(UUID uuid) {
        randomizerQueue.remove(uuid);

        for (Kit kit : kitManager.getKits()) {
            soloRankedQueue.get(kit).remove(uuid);
            soloUnrankedQueue.get(kit).remove(uuid);
        }
    }

    public void handleRandomizerQueue(Player player) {
        if (randomizerQueue.size() >= 1) {
            UUID opponent = randomizerQueue.poll();
            Player opponentPlayer = Bukkit.getPlayer(opponent);
            if (opponentPlayer == null) {
                addToRandomizerQueue(player);
                return;
            }

            User opponentUser = userManager.getUser(opponent);
            if (opponentUser == null) {
                addToRandomizerQueue(player);
                return;
            }

            BaseArena baseArena = mapManager.getSoloMap(true);
            if (baseArena == null) {
                removeFromQueue(opponentPlayer.getUniqueId());
                packetUtil.sendActionBar(player, ChatColor.RED + "We are currently out of arenas.");
                packetUtil.sendActionBar(opponentPlayer, ChatColor.RED + "We are currently out of arenas.");
                return;
            }

            handleRandomizerMatchStart(player, opponentPlayer, baseArena);
        } else addToRandomizerQueue(player);
    }

    private void handleRandomizerMatchStart(Player playerOne, Player playerTwo, BaseArena baseArena) {
        Match match = new Match(playerOne.getUniqueId(), playerTwo.getUniqueId(), playerOne.getDisplayName(), playerTwo.getDisplayName(), false, true);

        User userOne = userManager.getUser(playerOne.getUniqueId());
        if (userOne == null)
            return;

        User userTwo = userManager.getUser(playerTwo.getUniqueId());
        if (userTwo == null)
            return;

        handlePlayerMatchStart(playerOne, userOne, baseArena.getSpawnOne());
        handlePlayerMatchStart(playerTwo, userTwo, baseArena.getSpawnTwo());

        boolean buildOne = randomizer.giveKit(playerOne);
        boolean buildTwo = randomizer.giveKit(playerTwo);

        if (buildOne)
            noHealthRegen.add(playerOne.getUniqueId());

        if (buildTwo)
            noHealthRegen.add(playerTwo.getUniqueId());

        addMatch(playerOne.getUniqueId(), match);
        addMatch(playerTwo.getUniqueId(), match);

        matchTimerTask.addMatch(match);
    }

    public void handleRandomizerMatchEnd(Match match, UUID winner) {
        UUID loser = match.getPlayerOne() == winner ? match.getPlayerTwo() : match.getPlayerOne();

        noHealthRegen.remove(winner);

        noHealthRegen.remove(loser);

        handleSoloMatchEnd(winner, loser,
                (loser != match.getPlayerOne() ? match.getPlayerOneName() : match.getPlayerTwoName()),
                (loser == match.getPlayerOne() ? match.getPlayerOneName() : match.getPlayerTwoName()),
                match.isRanked(), match.getKit());
    }

    private void handleSoloMatchStart(Player playerOne, Player playerTwo, Kit kit, boolean ranked, BaseArena baseArena) {
        Match match = new Match(playerOne.getUniqueId(), playerTwo.getUniqueId(), playerOne.getDisplayName(), playerTwo.getDisplayName(), ranked, false, kit);

        User userOne = userManager.getUser(playerOne.getUniqueId());
        if (userOne == null)
            return;

        User userTwo = userManager.getUser(playerTwo.getUniqueId());
        if (userTwo == null)
            return;

        if (ranked) {
            if (userOne.getMatchesRemaining() != -1)
                userOne.decrementMatchesRemaining();
            if (userTwo.getMatchesRemaining() != -1)
                userTwo.decrementMatchesRemaining();
        }

        handlePlayerMatchStart(playerOne, userOne, baseArena.getSpawnOne());
        handlePlayerMatchStart(playerTwo, userTwo, baseArena.getSpawnTwo());

        if (userOne.getInventoryLayout(kit) == null)
            kit.giveKit(playerOne);
        else
            playerOne.getInventory().setContents(userOne.getInventoryLayout(kit));
        if (userOne.getInventoryLayout(kit) == null)
            kit.giveKit(playerTwo);
        else
            playerTwo.getInventory().setContents(userTwo.getInventoryLayout(kit));

        playerOne.getInventory().setArmorContents(kit.getArmourContents());
        playerTwo.getInventory().setArmorContents(kit.getArmourContents());

        playerOne.updateInventory();
        playerTwo.updateInventory();

        if (!kit.isRegen()) {
            noHealthRegen.add(playerOne.getUniqueId());
            noHealthRegen.add(playerTwo.getUniqueId());
        }

        if (!kit.isBuilding()) {
            noBuild.add(playerOne.getUniqueId());
            noBuild.add(playerTwo.getUniqueId());
        }

        if (!kit.isHunger()) {
            noHunger.add(playerOne.getUniqueId());
            noHunger.add(playerTwo.getUniqueId());
        }

        matchTimerTask.addMatch(match);

        addMatch(playerOne.getUniqueId(), match);
        addMatch(playerTwo.getUniqueId(), match);
    }

    public void handlePartyMatchStart(Party partyOne, Party partyTwo, Kit kit, boolean ranked, BaseArena baseArena) {
        PartyMatch partyMatch = new PartyMatch(partyOne, partyTwo, Bukkit.getPlayer(partyOne.getPartyLeader()).getName(), Bukkit.getPlayer(partyTwo.getPartyLeader()).getName(), kit, ranked);

        handleParty(partyOne, kit, baseArena, true);
        handleParty(partyTwo, kit, baseArena, false);

        partyTimerTask.addPartyMatch(partyMatch);

        addMatch(partyOne, partyMatch);
        addMatch(partyTwo, partyMatch);

    }

    private void handleParty(Party party, Kit kit, BaseArena baseArena, boolean oneOrTwo) {
        Player player;
        User user;
        for (UUID uuid : party.getPartyMembers()) {
            player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            user = userManager.getUser(uuid);
            if (user == null)
                return;

            handlePlayerMatchStart(player, user, oneOrTwo ? baseArena.getSpawnOne() : baseArena.getSpawnTwo());

            if (user.getInventoryLayout(kit) == null)
                kit.giveKit(player);
            else
                player.getInventory().setContents(user.getInventoryLayout(kit));

            player.getInventory().setArmorContents(kit.getArmourContents());

            player.updateInventory();

            if (!kit.isRegen())
                noHealthRegen.add(uuid);

            if (!kit.isBuilding())
                noBuild.add(uuid);

            if (!kit.isHunger())
                noHunger.add(uuid);
        }
    }

    public void handleSoloMatchEnd(Match match, UUID winner) {
        UUID loser = match.getPlayerOne() == winner ? match.getPlayerTwo() : match.getPlayerOne();

        handleRemoval(winner);

        handleRemoval(loser);

        handleSoloMatchEnd(winner, loser,
                (loser != match.getPlayerOne() ? match.getPlayerOneName() : match.getPlayerTwoName()),
                (loser == match.getPlayerOne() ? match.getPlayerOneName() : match.getPlayerTwoName()),
                match.isRanked(), match.getKit());
    }

    public void handlePartyMatchEnd(PartyMatch partyMatch, Party winner) {
        Party loser = partyMatch.getPartyOne() == winner ? partyMatch.getPartyTwo() : partyMatch.getPartyOne();

        winner.getPartyMembers().forEach(this::handleRemoval);
        loser.getPartyMembers().forEach(this::handleRemoval);

        handlePartyMatchEnd(winner, loser,
                (loser == partyMatch.getPartyOne() ? partyMatch.getPartyOneLeaderName() : partyMatch.getPartyTwoLeaderName()),
                (loser != partyMatch.getPartyOne() ? partyMatch.getPartyOneLeaderName() : partyMatch.getPartyTwoLeaderName()),
                partyMatch.isRanked(), partyMatch.getKit());
    }

    private void handleRemoval(UUID uuid) {
        noHealthRegen.remove(uuid);
        noBuild.remove(uuid);
        noHunger.remove(uuid);
    }

    private void handlePlayerMatchStart(Player player, User user, Location location) {
        player.teleport(location);

        player.setGameMode(GameMode.SURVIVAL);

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 10000000, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 10000000, false));

        user.setUserState(UserState.MATCH);
    }

    private void handlePartyMatchEnd(Party winner, Party loser, String winnerName, String loserName, boolean ranked, Kit kit) {
        winner.removeAllPotionEffects();

        loser.removeAllPotionEffects();

        if (ranked) {
            int ratingChange = calculateRatingChange(winner.getElo(kit), loser.getElo(kit));
            winner.setElo(kit, winner.getElo(kit) + ratingChange);
            loser.setElo(kit, loser.getElo(kit) - ratingChange);

            winner.broadcastMessage(constructRankedMatchEndMessage(loserName, true, winner.getElo(kit), ratingChange));
            loser.broadcastMessage(constructRankedMatchEndMessage(winnerName, false, loser.getElo(kit), ratingChange));

            sqlManager.saveStats(userManager.getUser(winner.getPartyMembers().get(0)).getId(), userManager.getUser(winner.getPartyMembers().get(1)).getId(), winner.getElo(kit), kit.getId());
            sqlManager.saveStats(userManager.getUser(loser.getPartyMembers().get(0)).getId(), userManager.getUser(loser.getPartyMembers().get(1)).getId(), loser.getElo(kit), kit.getId());
        } else {
            winner.broadcastMessage(constructUnrankedMatchEndMessage(loserName, true));
            loser.broadcastMessage(constructUnrankedMatchEndMessage(winnerName, false));
        }

        winner.getPartyMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20.0);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.setExp(0);
            player.setFireTicks(0);

            User user = userManager.getUser(uuid);
            if (user == null)
                return;

            user.setUserState(UserState.LOBBY);

            arena.addForTeleport(player);

        });

        loser.getPartyMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.updateInventory();

            player.setAllowFlight(true);
            player.setFlying(true);

            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20.0);
            player.setSaturation(20);
            player.setFoodLevel(20);
            player.setExp(0);
            player.setFireTicks(0);

            User user = userManager.getUser(uuid);
            if (user == null)
                return;

            user.setUserState(UserState.LOBBY);

            arena.addForTeleport(player);
        });

        removeMatch(winner);
        removeMatch(loser);
    }

    private void handleSoloMatchEnd(UUID winner, UUID loser, String winnerName, String loserName, boolean ranked, Kit kit) {
        Player winningPlayer = Bukkit.getPlayer(winner);
        for (PotionEffect potionEffect : winningPlayer.getActivePotionEffects())
            winningPlayer.removePotionEffect(potionEffect.getType());

        Player losingPlayer = Bukkit.getPlayer(loser);

        User winningUser = userManager.getUser(winner);
        if (winningUser == null)
            winningUser = sqlManager.loadUser(winner);

        User losingUser = userManager.getUser(loser);
        if (losingUser == null)
            losingUser = sqlManager.loadUser(winner);

        if (ranked) {
            int ratingChange = calculateRatingChange(winningUser.getElo(kit), losingUser.getElo(kit));
            winningUser.setElo(kit, winningUser.getElo(kit) + ratingChange);
            losingUser.setElo(kit, losingUser.getElo(kit) - ratingChange);

            winningPlayer.sendMessage(constructRankedMatchEndMessage(loserName, winningPlayer.getHealth(), true, ratingChange, winningUser.getElo(kit)));

            if (losingPlayer != null)
                losingPlayer.sendMessage(constructRankedMatchEndMessage(winningPlayer.getName(), winningPlayer.getHealth(), false, ratingChange, losingUser.getElo(kit)));

            sqlManager.saveStats(winningUser.getId(), winningUser.getElo(kit), kit.getId());
            sqlManager.saveStats(losingUser.getId(), losingUser.getElo(kit), kit.getId());
        } else {
            winningPlayer.sendMessage(constructUnrankedMatchEndMessage(loserName, winningPlayer.getHealth(), true));
            if (losingPlayer != null)
                losingPlayer.sendMessage(constructUnrankedMatchEndMessage(winningPlayer.getName(), winningPlayer.getHealth(), false));
        }

        winningPlayer.setAllowFlight(true);
        winningPlayer.setFlying(true);

        winningPlayer.setGameMode(GameMode.ADVENTURE);
        winningPlayer.setHealth(20.0);
        winningPlayer.setSaturation(20);
        winningPlayer.setFoodLevel(20);
        winningPlayer.setExp(0);
        winningPlayer.setFireTicks(0);

        arena.addForTeleport(winningPlayer);

        if (losingPlayer != null) {
            losingPlayer.getInventory().clear();
            losingPlayer.getInventory().setArmorContents(null);

            losingPlayer.updateInventory();

            losingPlayer.setAllowFlight(true);
            losingPlayer.setFlying(true);

            losingPlayer.setGameMode(GameMode.ADVENTURE);
            losingPlayer.setHealth(20.0);
            losingPlayer.setSaturation(20);
            losingPlayer.setFoodLevel(20);
            losingPlayer.setExp(0);
            losingPlayer.setFireTicks(0);

            losingPlayer.getActivePotionEffects().forEach(potionEffect -> losingPlayer.removePotionEffect(potionEffect.getType()));
            arena.addForTeleport(losingPlayer);
        }

        winningUser.setUserState(UserState.LOBBY);
        losingUser.setUserState(UserState.LOBBY);

        removeMatch(winner);
        removeMatch(loser);
    }

    private String constructUnrankedMatchEndMessage(String opponent, double health, boolean win) {
        return arena.getPrefix() + "You " + (win ? "defeated " : "lost to ") + ChatColor.RED + opponent + ChatColor.YELLOW + " with " + ChatColor.RED + decimalFormat.format(health) + "❤";
    }

    private String constructUnrankedMatchEndMessage(String opponent, boolean win) {
        return arena.getPrefix() + "You " + (win ? "defeated " : "lost to ") + ChatColor.RED + opponent;
    }

    private String[] constructRankedMatchEndMessage(String opponent, double health, boolean win, int eloChange, int newElo) {
        String loseOrGain = win ? ChatColor.YELLOW + "You gained " : ChatColor.YELLOW + "You lost ";
        String[] rankedString = new String[3];
        rankedString[0] = arena.getPrefix() + "You " + (win ? "defeated " : "lost to ") + ChatColor.RED + opponent + ChatColor.YELLOW + " with " + ChatColor.RED + decimalFormat.format(health) + "❤";
        rankedString[1] = arena.getPrefix() + loseOrGain + ChatColor.RED + eloChange + ChatColor.YELLOW + " ELO";
        rankedString[2] = arena.getPrefix() + ChatColor.YELLOW + "Your new rating is " + ChatColor.RED + "[" + newElo + "]";

        return rankedString;
    }

    private String[] constructRankedMatchEndMessage(String opponent, boolean win, int eloChange, int newElo) {
        String loseOrGain = win ? ChatColor.YELLOW + "You gained " : ChatColor.YELLOW + "You lost ";
        String[] rankedString = new String[3];
        rankedString[0] = arena.getPrefix() + "You " + (win ? "defeated " : "lost to ") + ChatColor.RED + opponent;
        rankedString[1] = arena.getPrefix() + loseOrGain + ChatColor.RED + eloChange + ChatColor.YELLOW + " ELO";
        rankedString[2] = arena.getPrefix() + ChatColor.YELLOW + "Your new rating is " + ChatColor.RED + "[" + newElo + "]";

        return rankedString;
    }

    public List<UUID> getNoHealthRegen() {
        return noHealthRegen;
    }

    public List<UUID> getNoBuild() {
        return noBuild;
    }

    public List<UUID> getNoHunger() {
        return noHunger;
    }

    private int calculateRatingChange(int winner, int loser) {
        double k = 32;
        double eloDifference = (double) loser - (double) winner;
        double x = eloDifference / 400;
        double y = Math.pow(10, x);
        double z = 1 + y;
        double a  = 1 / z;
        double b = 1 - a;
        double c = k * b;
        double win = Math.round(c);
        if (win == 0)
            win = 1;
        return (int) win;
    }

    public Map<UUID, Match> getMatchMap() {
        return matchMap;
    }
}
