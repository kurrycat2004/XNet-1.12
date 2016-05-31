package mcjty.xnet.blocks.controller;

import elec332.core.api.annotations.RegisterTile;
import elec332.core.tile.TileBase;
import elec332.core.world.WorldHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.init.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 30-5-2016.
 */
@RegisterTile(name = "TileEntityXNetInactiveController")
public final class TileEntityInactiveController extends TileBase {

    public TileEntityInactiveController(){
    }

    private NBTTagCompound controllerTag;

    protected void readFromActive(NBTTagCompound tag){
        controllerTag = tag;
    }

    public void activate(){
        final NBTTagCompound old = controllerTag;
        final World world = worldObj;
        final BlockPos pos = this.pos;
        WorldHelper.setBlockState(world, pos, ModBlocks.controllerBlock.getDefaultState(), 3);
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileEntityController){
            ((TileEntityController) tile).readItemStackNBT(old);
        }
    }

    @Override
    public void writeToItemStack(NBTTagCompound tagCompound) {
        super.writeToItemStack(tagCompound);
        tagCompound.setTag("activeInfo", controllerTag);
    }

    @Override
    public void readItemStackNBT(NBTTagCompound tagCompound) {
        super.readItemStackNBT(tagCompound);
        controllerTag = tagCompound.getCompoundTag("activeInfo");
    }

}
