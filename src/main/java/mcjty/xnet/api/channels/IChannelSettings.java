package mcjty.xnet.api.channels;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Channel specific settings. This is a server-side object only
 */
public interface IChannelSettings {

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);
}
