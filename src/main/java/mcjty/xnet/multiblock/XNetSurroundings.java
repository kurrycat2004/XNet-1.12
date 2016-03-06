package mcjty.xnet.multiblock;

import elec332.core.world.WorldHelper;
import mcjty.xnet.api.XNetAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import static mcjty.xnet.api.XNetAPI.*;

/**
 * Created by Elec332 on 6-3-2016.
 */
public final class XNetSurroundings {

    public XNetSurroundings(BlockPos pos, World world, EnumFacing facing){
        cable = false;
        init(pos, world, facing);
    }

    private void init(BlockPos pos, World world, EnumFacing facing){
        BlockPos other = pos.offset(facing);
        TileEntity tile = WorldHelper.chunkLoaded(world, other) ? WorldHelper.getTileAt(world, other) : null;
        if (tile != null){
            if (tile.hasCapability(XNET_CABLE_CAPABILITY, null)){
                cable = tile.getCapability(XNET_CABLE_CAPABILITY, null).canConnectToSide(facing);
            }
        }
    }

    private boolean cable; //Not checking compatibility, just connectivity

    @Override
    public boolean equals(Object obj) {
        return obj instanceof XNetSurroundings && areEqual((XNetSurroundings) obj);
    }

    public boolean areEqual(XNetSurroundings other){
        return other != null && other.cable == cable;
    }

}
