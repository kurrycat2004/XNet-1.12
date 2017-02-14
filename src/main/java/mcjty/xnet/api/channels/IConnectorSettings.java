package mcjty.xnet.api.channels;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

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
     * Return a one-char indicator of the current status
     */
    String getIndicator();

    /**
     * Return true if a tag is enabled given the current settings
     */
    boolean isEnabled(String tag);

    /**
     * Create the gui for this connector and fill with the current values or
     * defaults if it is not set yet. This is called client-side.
     */
    void createGui(IEditorGui gui);

    /**
     * If something changes on the gui then this will be called server
     * side with a map for all gui components
     */
    void update(Map<String, Object> data);
}
