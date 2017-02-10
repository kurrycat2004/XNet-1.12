package mcjty.xnet.apiimpl;

import mcjty.xnet.api.channels.IChannelSettings;
import net.minecraft.nbt.NBTTagCompound;

public class ItemChannelSettings implements IChannelSettings {

    enum ChannelMode {
        FIRST,
        ROUNDROBIN,
        RANDOM
    }

    private ChannelMode channelMode = ChannelMode.FIRST;

    public ChannelMode getChannelMode() {
        return channelMode;
    }

    public void setChannelMode(ChannelMode channelMode) {
        this.channelMode = channelMode;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        channelMode = ChannelMode.values()[tag.getByte("mode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("mode", (byte) channelMode.ordinal());
    }
}
