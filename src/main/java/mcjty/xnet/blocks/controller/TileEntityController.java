package mcjty.xnet.blocks.controller;

import elec332.core.api.annotations.RegisterTile;
import elec332.core.tile.TileBase;
import elec332.core.world.WorldHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.api.IXNetController;
import mcjty.xnet.init.ModBlocks;
import mcjty.xnet.multiblock.XNetGridController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

@RegisterTile(name = "TileEntityXNetController")
public final class TileEntityController extends TileBase {

    public TileEntityController(){
        controller = new XNetGridController(this);
    }

    private final XNetGridController controller;

    public void deactivate(){
        final World world = worldObj;
        final BlockPos pos = this.pos;
        final NBTTagCompound nbt = new NBTTagCompound();
        writeToItemStack(nbt);
        WorldHelper.setBlockState(world, pos, ModBlocks.controllerBlock.getDefaultState().withProperty(ControllerBlock.ACTIVE_PROPERTY, false), 3);
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileEntityInactiveController){
            ((TileEntityInactiveController) tile).readFromActive(nbt);
        }
    }

    @Override
    public void readItemStackNBT(NBTTagCompound tagCompound) {
        super.readItemStackNBT(tagCompound);
        controller.deserializeNBT(tagCompound.getCompoundTag("controllerData"));
    }

    @Override
    public void writeToItemStack(NBTTagCompound tagCompound) {
        super.writeToItemStack(tagCompound);
        tagCompound.setTag("controllerData", controller.serializeNBT());
    }

    @Nonnull
    public IXNetController getController(){
        return controller;
    }

}
