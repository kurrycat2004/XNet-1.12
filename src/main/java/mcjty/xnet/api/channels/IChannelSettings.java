package mcjty.xnet.api.channels;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

/**
 * Channel specific settings
 */
public interface IChannelSettings {

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);

    /**
     * Create the gui for this channel and fill with the current values or
     * defaults if it is not set yet. This is called client-side.
     */
    void createGui(IEditorGui gui);

    /**
     * If something changes on the gui then this will be called server
     * side with a map for all gui components
     */
    void update(Map<String, Object> data);
}
