package mcjty.xnet.api.channels;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Channel type specific connector settings. This is a server-side object only
 */
public interface IConnectorSettings {

    /**
     * Return true if this connector need the ghost slots from the controller
     */
    boolean supportsGhostSlots();

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);
}
