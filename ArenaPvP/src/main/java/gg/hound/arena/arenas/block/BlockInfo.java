package gg.hound.arena.arenas.block;

import org.bukkit.Location;

public class BlockInfo {

    private final Location location;

    private final int blockTypeID;

    private final byte blockData;

    public BlockInfo(Location location, int blockTypeID, byte blockData) {
        this.location = location;
        this.blockTypeID = blockTypeID;
        this.blockData = blockData;
    }

    public Location getLocation() {
        return location;
    }

    public int getBlockTypeID() {
        return blockTypeID;
    }

    public byte getBlockData() {
        return blockData;
    }
}
