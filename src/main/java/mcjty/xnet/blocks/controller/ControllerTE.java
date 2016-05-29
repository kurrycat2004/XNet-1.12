package mcjty.xnet.blocks.controller;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.channel.Channel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControllerTE extends GenericTileEntity {

    private final List<Channel> channels = new ArrayList<>();
    private final List<Channel> channels_ = Collections.unmodifiableList(channels);

    public List<Channel> getChannels() {
        return channels_;
    }

    public void addChannel(String name) {
        Channel channel = new Channel();
        channel.setName(name);
        channels.add(channel);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        NBTTagList channelList = new NBTTagList();
        for (Channel channel : channels) {
            NBTTagCompound child = new NBTTagCompound();
            channel.writeToNBT(child);
            channelList.appendTag(child);
        }
        tagCompound.setTag("channels", channelList);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        NBTTagList channelList = tagCompound.getTagList("channels", Constants.NBT.TAG_COMPOUND);
        channels.clear();
        for (int i = 0 ; i < channelList.tagCount() ; i++) {
            NBTTagCompound child = (NBTTagCompound) channelList.get(i);
            Channel channel = new Channel();
            channel.readFromNBT(child);
            channels.add(channel);
        }
    }

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
