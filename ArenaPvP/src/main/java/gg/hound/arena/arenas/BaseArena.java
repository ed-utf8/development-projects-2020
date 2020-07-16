package gg.hound.arena.arenas;

import gg.hound.arena.arenas.block.BlockInfo;
import org.bukkit.Location;

import java.util.List;

public class BaseArena {

    private final String mapName;
    private final String mapCreator;

    private final Location spawnOne;
    private final Location spawnTwo;

    private final List<BlockInfo> blockInfos;

    private final int multiplier;

    private ArenaState arenaState = ArenaState.PASTING;

    public BaseArena(String mapName, String mapCreator, Location spawnOne, Location spawnTwo, List<BlockInfo> blockInfos, int multiplier) {
        this.mapName = mapName;
        this.mapCreator = mapCreator;
        this.spawnOne = spawnOne;
        this.spawnTwo = spawnTwo;
        this.blockInfos = blockInfos;
        this.multiplier = multiplier;
    }

    public String getMapName() {
        return mapName;
    }

    public String getMapCreator() {
        return mapCreator;
    }

    public List<BlockInfo> getBlockInfos() {
        return blockInfos;
    }

    public Location getSpawnOne() {
        return spawnOne;
    }

    public Location getSpawnTwo() {
        return spawnTwo;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }
}
