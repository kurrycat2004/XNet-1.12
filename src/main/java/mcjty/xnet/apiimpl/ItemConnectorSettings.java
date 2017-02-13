package mcjty.xnet.apiimpl;

import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IEditorGui;
import net.minecraft.nbt.NBTTagCompound;

public class ItemConnectorSettings implements IConnectorSettings {

    enum ItemMode {
        INSERT,
        EXTRACT
    }

    enum OredictMode {
        ON,
        OFF
    }

    enum MetaMode {
        ON,
        OFF
    }

    private ItemMode itemMode = ItemMode.INSERT;
    private OredictMode oredictMode = OredictMode.OFF;
    private MetaMode metaMode = MetaMode.OFF;

    public ItemMode getItemMode() {
        return itemMode;
    }

    public void setItemMode(ItemMode itemMode) {
        this.itemMode = itemMode;
    }

    public OredictMode getOredictMode() {
        return oredictMode;
    }

    public void setOredictMode(OredictMode oredictMode) {
        this.oredictMode = oredictMode;
    }

    public MetaMode getMetaMode() {
        return metaMode;
    }

    public void setMetaMode(MetaMode metaMode) {
        this.metaMode = metaMode;
    }

    @Override
    public boolean supportsGhostSlots() {
        return true;
    }

    @Override
    public void createGui(IEditorGui gui) {
        gui
                .choices(itemMode, ItemMode.values()).redstoneMode(null).nl()
                .label("OD").choices(oredictMode, OredictMode.values())
                .label("Meta").choices(metaMode, MetaMode.values());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        itemMode = ItemMode.values()[tag.getByte("itemMode")];
        oredictMode = OredictMode.values()[tag.getByte("oredictMode")];
        metaMode = MetaMode.values()[tag.getByte("metaMode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("itemMode", (byte) itemMode.ordinal());
        tag.setByte("oredictMode", (byte) oredictMode.ordinal());
        tag.setByte("metaMode", (byte) metaMode.ordinal());
    }
}
