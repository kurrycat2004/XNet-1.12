package mcjty.xnet.blocks.controller;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ControllerTE extends GenericTileEntity {


    private class XNetComponent implements IXNetComponent {
        private int id;
        private ControllerTE te;

        public XNetComponent(ControllerTE te) {
            this.te = te;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setId(int id) {
            System.out.println("id = " + id);
            this.id = id;
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
