package mcjty.xnet.apiimpl;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IEditorGui;
import mcjty.xnet.api.channels.IndicatorIcon;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ItemConnectorSettings implements IConnectorSettings {

    public static final String TAG_ITEM = "item";
    public static final String TAG_OREDICT = "od";
    public static final String TAG_RS = "rs";
    public static final String TAG_META = "meta";
    public static final String TAG_PRIORITY = "priority";
    public static final String TAG_MAX = "max";

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
    private RSMode rsMode = RSMode.IGNORED;
    @Nullable private Integer priority = 0;
    @Nullable private Integer maxAmount = null;

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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    @Override
    public boolean supportsGhostSlots() {
        return true;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        switch (itemMode) {
            case INSERT:
                return new IndicatorIcon(GuiController.iconGuiElements, 0, 70, 10, 10);
            case EXTRACT:
                return new IndicatorIcon(GuiController.iconGuiElements, 10, 70, 10, 10);
        }
        return null;
    }

    @Override
    @Nullable
    public String getIndicator() {
        return null;
    }

    @Override
    public void createGui(IEditorGui gui) {
        gui
                .shift(10)
                .choices(TAG_ITEM, "Insert or extract mode", itemMode, ItemMode.values())
                .shift(10)
                .redstoneMode(TAG_RS, rsMode).nl()
                .label("Ore").choices(TAG_OREDICT, "If enabled the ore dictionary is used", oredictMode, OredictMode.values()).shift(10)
                .label("Meta").choices(TAG_META, "If enabled then metadata must match", metaMode, MetaMode.values()).nl()
                .label("Pri").integer(TAG_PRIORITY, "Insertion priority", priority).shift(10)
                .label("Max").integer(TAG_MAX, "Maximum number to insert/keep", maxAmount);
    }

    private static Set<String> INSERT_TAGS = ImmutableSet.of(TAG_ITEM, TAG_RS, TAG_MAX, TAG_PRIORITY);
    private static Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_ITEM, TAG_RS, TAG_OREDICT, TAG_META, TAG_MAX);

    @Override
    public boolean isEnabled(String tag) {
        if (itemMode == ItemMode.INSERT) {
            return INSERT_TAGS.contains(tag);
        } else {
            return EXTRACT_TAGS.contains(tag);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        itemMode = ItemMode.valueOf(((String)data.get(TAG_ITEM)).toUpperCase());
        oredictMode = OredictMode.valueOf(((String)data.get(TAG_OREDICT)).toUpperCase());
        metaMode = MetaMode.valueOf(((String)data.get(TAG_META)).toUpperCase());
        rsMode = RSMode.valueOf(((String)data.get(TAG_RS)).toUpperCase());
        priority = (Integer) data.get(TAG_PRIORITY);
        maxAmount = (Integer) data.get(TAG_MAX);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        itemMode = ItemMode.values()[tag.getByte("itemMode")];
        oredictMode = OredictMode.values()[tag.getByte("oredictMode")];
        metaMode = MetaMode.values()[tag.getByte("metaMode")];
        rsMode = RSMode.values()[tag.getByte("rsMode")];
        if (tag.hasKey("priority")) {
            priority = tag.getInteger("priority");
        } else {
            priority = null;
        }
        if (tag.hasKey("max")) {
            maxAmount = tag.getInteger("max");
        } else {
            maxAmount = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("itemMode", (byte) itemMode.ordinal());
        tag.setByte("oredictMode", (byte) oredictMode.ordinal());
        tag.setByte("metaMode", (byte) metaMode.ordinal());
        tag.setByte("rsMode", (byte) rsMode.ordinal());
        if (priority != null) {
            tag.setInteger("priority", priority);
        }
        if (maxAmount != null) {
            tag.setInteger("max", maxAmount);
        }
    }
}
