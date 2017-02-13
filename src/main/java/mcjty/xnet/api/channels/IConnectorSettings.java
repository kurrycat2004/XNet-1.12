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

    /**
     * Create the gui for this connector and fill with the current values or
     * defaults if it is not set yet
     */
    void createGui(IEditorGui gui);
}
