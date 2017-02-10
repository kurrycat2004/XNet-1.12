package mcjty.xnet.logic;

import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IChannelType;
import net.minecraft.nbt.NBTTagCompound;

public class ChannelInfo {

    public static final int MAX_CHANNELS = 8;

    private final IChannelType type;
    private IChannelSettings channelSettings;

    public ChannelInfo(IChannelType type) {
        this.type = type;
        channelSettings = type.createChannel();
    }

    public IChannelType getType() {
        return type;
    }

    public IChannelSettings getChannelSettings() {
        return channelSettings;
    }

    public void setChannelSettings(IChannelSettings channelSettings) {
        this.channelSettings = channelSettings;
    }

    public void writeToNBT(NBTTagCompound tag) {
        channelSettings.writeToNBT(tag);
    }

    public void readFromNBT(NBTTagCompound tag) {
        channelSettings.readFromNBT(tag);
    }
}
