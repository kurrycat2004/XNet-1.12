package mcjty.xnet.apiimpl;

import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IEditorGui;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class ItemChannelSettings implements IChannelSettings {

    public static final String TAG_MODE = "mode";

    enum ChannelMode {
        PRIORITY,
        ROUNDROBIN,
        RANDOM
    }

    private ChannelMode channelMode = ChannelMode.PRIORITY;

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

    @Override
    public void createGui(IEditorGui gui) {
        gui.choices(TAG_MODE, channelMode, ChannelMode.values());
    }

    @Override
    public void update(Map<String, Object> data) {
        channelMode = ChannelMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
    }
}
