package mcjty.xnet.apiimpl;

import com.google.common.collect.ImmutableSet;
import mcjty.lib.tools.ItemStackList;
import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ItemConnectorSettings implements IConnectorSettings {

    public static final String TAG_MODE = "mode";
    public static final String TAG_OREDICT = "od";
    public static final String TAG_RS = "rs";
    public static final String TAG_META = "meta";
    public static final String TAG_PRIORITY = "priority";
    public static final String TAG_MIN = "min";
    public static final String TAG_MAX = "max";
    public static final String TAG_FILTER = "f";
    public static final String TAG_BLACKLIST = "blacklist";

    public static final int FILTER_SIZE = 18;

    enum ItemMode {
        INSERT,
        EXTRACT
    }

    private ItemMode itemMode = ItemMode.INSERT;
    private boolean oredictMode = false;
    private boolean metaMode = false;
    private RSMode rsMode = RSMode.IGNORED;
    private boolean blacklist = false;
    @Nullable private Integer priority = 0;
    @Nullable private Integer minAmount = null;
    @Nullable private Integer maxAmount = null;
    private ItemStackList filters = ItemStackList.create(FILTER_SIZE);

    public ItemMode getItemMode() {
        return itemMode;
    }

    public void setItemMode(ItemMode itemMode) {
        this.itemMode = itemMode;
    }

    public boolean isOredictMode() {
        return oredictMode;
    }

    public void setOredictMode(boolean oredictMode) {
        this.oredictMode = oredictMode;
    }

    public boolean isMetaMode() {
        return metaMode;
    }

    public void setMetaMode(boolean metaMode) {
        this.metaMode = metaMode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Nullable
    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(@Nullable Integer minAmount) {
        this.minAmount = minAmount;
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
                return new IndicatorIcon(GuiController.iconGuiElements, 0, 70, 13, 10);
            case EXTRACT:
                return new IndicatorIcon(GuiController.iconGuiElements, 13, 70, 13, 10);
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
                .choices(TAG_MODE, "Insert or extract mode", itemMode, ItemMode.values())
                .shift(10)
                .redstoneMode(TAG_RS, rsMode).nl()
                .label("Pri").integer(TAG_PRIORITY, "Insertion priority", priority).shift(10)
                .label("Keep")
                .integer(TAG_MIN, "Minimum number to insert/keep", minAmount)
                .integer(TAG_MAX, "Maximum number to insert/keep", maxAmount).nl()
                .toggleText(TAG_BLACKLIST, "Enable blacklist mode", "BL", blacklist).shift(5)
                .toggleText(TAG_OREDICT, "Ore dictionary matching", "Ore", oredictMode).shift(5)
                .toggleText(TAG_META, "Metadata matching", "Meta", metaMode).nl();
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            gui.ghostSlot(TAG_FILTER + i, filters.get(i));
        }
    }

    private static Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_MIN, TAG_MAX, TAG_PRIORITY, TAG_OREDICT, TAG_META, TAG_BLACKLIST);
    private static Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_MIN, TAG_MAX, TAG_OREDICT, TAG_META, TAG_BLACKLIST);

    @Override
    public boolean isEnabled(String tag) {
        if (tag.startsWith(TAG_FILTER)) {
            return true;
        }
        if (itemMode == ItemMode.INSERT) {
            return INSERT_TAGS.contains(tag);
        } else {
            return EXTRACT_TAGS.contains(tag);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        itemMode = ItemMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        oredictMode = Boolean.TRUE.equals(data.get(TAG_OREDICT));
        metaMode = Boolean.TRUE.equals(data.get(TAG_META));
        rsMode = RSMode.valueOf(((String)data.get(TAG_RS)).toUpperCase());
        blacklist = Boolean.TRUE.equals(data.get(TAG_BLACKLIST));
        priority = (Integer) data.get(TAG_PRIORITY);
        minAmount = (Integer) data.get(TAG_MIN);
        maxAmount = (Integer) data.get(TAG_MAX);
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            filters.set(i, (ItemStack) data.get(TAG_FILTER+i));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        itemMode = ItemMode.values()[tag.getByte("itemMode")];
        oredictMode = tag.getBoolean("oredictMode");
        metaMode = tag.getBoolean("metaMode");
        rsMode = RSMode.values()[tag.getByte("rsMode")];
        blacklist = tag.getBoolean("blacklist");
        if (tag.hasKey("priority")) {
            priority = tag.getInteger("priority");
        } else {
            priority = null;
        }
        if (tag.hasKey("min")) {
            minAmount = tag.getInteger("min");
        } else {
            minAmount = null;
        }
        if (tag.hasKey("max")) {
            maxAmount = tag.getInteger("max");
        } else {
            maxAmount = null;
        }
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            if (tag.hasKey("filter" + i)) {
                NBTTagCompound itemTag = tag.getCompoundTag("filter" + i);
                filters.set(i, ItemStackTools.loadFromNBT(itemTag));
            } else {
                filters.set(i, ItemStackTools.getEmptyStack());
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("itemMode", (byte) itemMode.ordinal());
        tag.setBoolean("oredictMode", oredictMode);
        tag.setBoolean("metaMode", metaMode);
        tag.setByte("rsMode", (byte) rsMode.ordinal());
        tag.setBoolean("blacklist", blacklist);
        if (priority != null) {
            tag.setInteger("priority", priority);
        }
        if (minAmount != null) {
            tag.setInteger("min", minAmount);
        }
        if (maxAmount != null) {
            tag.setInteger("max", maxAmount);
        }
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            if (ItemStackTools.isValid(filters.get(i))) {
                NBTTagCompound itemTag = new NBTTagCompound();
                filters.get(i).writeToNBT(itemTag);
                tag.setTag("filter" + i, itemTag);
            }
        }
    }
}
