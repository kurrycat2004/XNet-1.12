package mcjty.xnet.multiblock;

import elec332.core.grid.capability.ITileData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

/**
 * Created by Elec332 on 6-3-2016.
 */
public class XNetTileData implements ITileData {

    protected XNetTileData(TileEntity tile){
        this.tile = tile;
    }

    private final TileEntity tile;

    @Override
    public BlockPos getPos() {
        return tile.getPos();
    }

    @Override
    public TileEntity getTile() {
        return tile;
    }

}
