package mcjty.xnet.blocks.controller;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.multiblock.XNetGridController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ControllerTE extends GenericTileEntity {

    private final XNetGridController controller;

    public ControllerTE() {
        controller = new XNetGridController(this);
    }

    public XNetGridController getController() {
        return controller;
    }

    public void addChannel(String name) {
//        Channel channel = findChannel(name);
//        if (channel != null) {
//            // Already exists
//            return;
//        }
//        channel = new Channel();
//        channel.setName(name);
//        channels.add(channel);
//        markDirty();
    }

//    private Channel findChannel(String name) {
//        for (Channel channel : channels) {
//            if (name.equals(channel.getName())) {
//                return channel;
//            }
//        }
//        return null;
//    }

    public void removeChannel(String name) {
//        Channel channel = findChannel(name);
//        if (channel != null) {
//            channels.remove(channel);
//            markDirty();
//        }
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

    private class XNetComponent extends IXNetComponent {
        private ControllerTE te;

        public XNetComponent(ControllerTE te) {
            this.te = te;
        }
    }

    private XNetComponent component = new XNetComponent(this);

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == XNetAPI.XNET_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == XNetAPI.XNET_CAPABILITY) {
            return (T) component;
        }
        return super.getCapability(capability, facing);
    }
}
