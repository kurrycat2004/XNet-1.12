package mcjty.xnet.api.channels;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Channel type specific connector settings
 */
public interface IConnectorSettings {

    /**
     * Return true if this connector need the ghost slots from the controller
     */
    boolean supportsGhostSlots();

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);
}
