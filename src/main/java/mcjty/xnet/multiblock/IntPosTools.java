package mcjty.xnet.multiblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class IntPosTools {

    public static int getX(int pos) {
        return pos & 0xf;
    }

    public static int getY(int pos) {
        return (pos >> 4) & 0xff;
    }

    public static int getZ(int pos) {
        return (pos >> 12) & 0xf;
    }

    public static int posSouth(int pos) {
        if (getZ(pos) >= 15) {
            return -1;
        }
        return pos + (1<<12);
    }

    public static int posNorth(int pos) {
        if (getZ(pos) <= 1) {
            return -1;
        }
        return pos - (1<<12);
    }

    public static int posEast(int pos) {
        if (getX(pos) >= 15) {
            return -1;
        }
        return pos+1;
    }

    public static int posWest(int pos) {
        if (getX(pos) <= 1) {
            return -1;
        }
        return pos-1;
    }

    public static int posUp(int pos) {
        if (getY(pos) >= 255) {
            return -1;
        }
        return pos + (1<<4);
    }

    public static int posDown(int pos) {
        if (getY(pos) <= 1) {
            return -1;
        }
        return pos - (1<<4);
    }

    public static int[] getSidePositions(int pos) {
        return new int[] { posDown(pos), posUp(pos), posEast(pos), posWest(pos), posSouth(pos), posNorth(pos) };
    }

    public static int posToInt(BlockPos pos) {
        int dx = pos.getX() & 0xf;
        int dy = pos.getY();
        int dz = pos.getZ() & 0xf;
        return dx << 12 | dy << 4 | dz;
    }

    public static BlockPos intToPos(ChunkPos cpos, int i) {
        int dx = getX(i);
        int dy = getY(i);
        int dz = getZ(i);
        return new BlockPos(cpos.chunkXPos | dx, dy, cpos.chunkZPos | dz);
    }
}
