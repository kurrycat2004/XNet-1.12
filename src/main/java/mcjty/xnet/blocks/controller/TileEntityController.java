package mcjty.xnet.blocks.controller;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.api.IXNetCable;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.handler.WorldHandler;
import mcjty.xnet.multiblock.XNetGridController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public final class TileEntityController extends GenericTileEntity {

    public TileEntityController() {
        controller = new XNetGridController(this);
        component = new ControllerCable();
    }

    private final XNetGridController controller;
    private final IXNetCable component;

    private boolean active;

    public XNetGridController getController() {
        return controller;
    }

    public void activate() {
        active = true;
        WorldHandler.instance.get(worldObj).xNetWorldGridRegistry.addTile(this);
        markDirty();
    }

    public void deactivate(){
        active = false;
        WorldHandler.instance.get(worldObj).xNetWorldGridRegistry.removeTile(this);
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setBoolean("b", active);
        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        this.active = tagCompound.getBoolean("b");
        super.readFromNBT(tagCompound);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setTag("controllerData", controller.serializeNBT());
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        controller.deserializeNBT(tagCompound.getCompoundTag("controllerData"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return active && capability == XNetAPI.XNET_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (active && capability == XNetAPI.XNET_CAPABILITY) {
            return (T) component;
        }
        return super.getCapability(capability, facing);
    }


    private class ControllerCable implements IXNetCable {

        @Override
        public boolean canConnectTo(IXNetCable otherCable) {
            return true;
        }

        @Override
        public boolean canConnectToSide(EnumFacing facing) {
            return true;
        }

    }

}
