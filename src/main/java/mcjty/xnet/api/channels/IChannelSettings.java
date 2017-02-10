package mcjty.xnet.api.channels;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Channel specific settings
 */
public interface IChannelSettings {

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);
}
