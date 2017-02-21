package mcjty.xnet.apiimpl.items;

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
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class ItemConnectorSettings implements IConnectorSettings {

    public static final String TAG_FACING = "facing";
    public static final String TAG_MODE = "mode";
    public static final String TAG_STACK = "stack";
    public static final String TAG_SPEED = "speed";
    public static final String TAG_OREDICT = "od";
    public static final String TAG_NBT = "nbt";
    public static final String TAG_RS = "rs";
    public static final String TAG_META = "meta";
    public static final String TAG_PRIORITY = "priority";
    public static final String TAG_COUNT = "count";
    public static final String TAG_FILTER = "flt";
    public static final String TAG_BLACKLIST = "blacklist";

    public static final int FILTER_SIZE = 18;

    enum ItemMode {
        INS,
        EXT
    }

    enum StackMode {
        SINGLE,
        STACK
    }

    private final boolean advanced;

    private ItemMode itemMode = ItemMode.INS;
    private int speed = 2;
    private StackMode stackMode = StackMode.SINGLE;
    private boolean oredictMode = false;
    private boolean metaMode = false;
    private boolean nbtMode = false;
    private RSMode rsMode = RSMode.IGNORED;
    private boolean blacklist = false;
    @Nullable private Integer priority = 0;
    @Nullable private Integer count = null;
    private ItemStackList filters = ItemStackList.create(FILTER_SIZE);

    @Nonnull private final EnumFacing side;
    @Nullable private EnumFacing facingOverride = null; // Only available on advanced connectors

    // Cached matcher for items
    private Predicate<ItemStack> matcher = null;

    public ItemMode getItemMode() {
        return itemMode;
    }

    @Nonnull
    public EnumFacing getFacing() {
        return facingOverride == null ? side : facingOverride;
    }

    public ItemConnectorSettings(boolean advanced, @Nonnull EnumFacing side) {
        this.advanced = advanced;
        this.side = side;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        switch (itemMode) {
            case INS:
                return new IndicatorIcon(GuiController.iconGuiElements, 0, 70, 13, 10);
            case EXT:
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
                .choices(TAG_FACING, "Side from which to operate", facingOverride == null ? side : facingOverride, EnumFacing.VALUES)
                .choices(TAG_MODE, "Insert or extract mode", itemMode, ItemMode.values())
                .choices(TAG_STACK, "Single item or entire stack", stackMode, StackMode.values())
                .choices(TAG_SPEED, "Number of ticks for each operation", Integer.toString(speed * 10),
                        "10", "20", "60", "100", "200")
                .nl()

                .label("Pri").integer(TAG_PRIORITY, "Insertion priority", priority).shift(10)
                .label("Count")
                .integer(TAG_COUNT, itemMode == ItemMode.EXT ? "Amount in destination inventory to keep" : "Max amount in destination inventory", count)
                .shift(10)
                .redstoneMode(TAG_RS, rsMode)
                .nl()

                .toggleText(TAG_BLACKLIST, "Enable blacklist mode", "BL", blacklist).shift(2)
                .toggleText(TAG_OREDICT, "Ore dictionary matching", "Ore", oredictMode).shift(2)
                .toggleText(TAG_META, "Metadata matching", "Meta", metaMode).shift(2)
                .toggleText(TAG_NBT, "NBT matching", "NBT", nbtMode).nl();
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            gui.ghostSlot(TAG_FILTER + i, filters.get(i));
        }
    }

    public Predicate<ItemStack> getMatcher() {
        if (matcher == null) {
            ItemStackList filterList = ItemStackList.create();
            for (ItemStack stack : filters) {
                if (ItemStackTools.isValid(stack)) {
                    filterList.add(stack);
                }
            }
            if (filterList.isEmpty()) {
                matcher = itemStack -> true;
            } else {
                ItemFilterCache filterCache = new ItemFilterCache(metaMode, oredictMode, blacklist, nbtMode, filterList);
                matcher = filterCache::match;
            }
        }
        return matcher;
    }

    public StackMode getStackMode() {
        return stackMode;
    }

    @Nonnull
    public Integer getPriority() {
        return priority == null ? 0 : priority;
    }

    @Nullable
    public Integer getCount() {
        return count;
    }

    public int getSpeed() {
        return speed;
    }

    public RSMode getRsMode() {
        return rsMode;
    }

    private static Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COUNT, TAG_PRIORITY, TAG_OREDICT, TAG_META, TAG_NBT, TAG_BLACKLIST);
    private static Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COUNT, TAG_OREDICT, TAG_META, TAG_NBT, TAG_BLACKLIST, TAG_STACK, TAG_SPEED);

    @Override
    public boolean isEnabled(String tag) {
        if (tag.startsWith(TAG_FILTER)) {
            return true;
        }
        if (tag.equals(TAG_FACING)) {
            return advanced;
        }
        if (itemMode == ItemMode.INS) {
            return INSERT_TAGS.contains(tag);
        } else {
            return EXTRACT_TAGS.contains(tag);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        itemMode = ItemMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        stackMode = StackMode.valueOf(((String)data.get(TAG_STACK)).toUpperCase());
        speed = Integer.parseInt((String) data.get(TAG_SPEED)) / 10;
        if (speed == 0) {
            speed = 2;
        }
        oredictMode = Boolean.TRUE.equals(data.get(TAG_OREDICT));
        metaMode = Boolean.TRUE.equals(data.get(TAG_META));
        nbtMode = Boolean.TRUE.equals(data.get(TAG_NBT));
        rsMode = RSMode.valueOf(((String)data.get(TAG_RS)).toUpperCase());
        blacklist = Boolean.TRUE.equals(data.get(TAG_BLACKLIST));
        priority = (Integer) data.get(TAG_PRIORITY);
        count = (Integer) data.get(TAG_COUNT);
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            filters.set(i, (ItemStack) data.get(TAG_FILTER+i));
        }
        matcher = null;
        String facing = (String) data.get(TAG_FACING);
        facingOverride = facing == null ? null : EnumFacing.byName(facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        itemMode = ItemMode.values()[tag.getByte("itemMode")];
        stackMode = StackMode.values()[tag.getByte("stackMode")];
        speed = tag.getInteger("speed");
        if (speed == 0) {
            speed = 2;
        }
        oredictMode = tag.getBoolean("oredictMode");
        metaMode = tag.getBoolean("metaMode");
        nbtMode = tag.getBoolean("nbtMode");
        rsMode = RSMode.values()[tag.getByte("rsMode")];
        blacklist = tag.getBoolean("blacklist");
        if (tag.hasKey("priority")) {
            priority = tag.getInteger("priority");
        } else {
            priority = null;
        }
        if (tag.hasKey("count")) {
            count = tag.getInteger("count");
        } else {
            count = null;
        }
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            if (tag.hasKey("filter" + i)) {
                NBTTagCompound itemTag = tag.getCompoundTag("filter" + i);
                filters.set(i, ItemStackTools.loadFromNBT(itemTag));
            } else {
                filters.set(i, ItemStackTools.getEmptyStack());
            }
        }
        matcher = null;
        if (tag.hasKey("facingOverride")) {
            facingOverride = EnumFacing.VALUES[tag.getByte("facingOverride")];
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("itemMode", (byte) itemMode.ordinal());
        tag.setByte("stackMode", (byte) stackMode.ordinal());
        tag.setInteger("speed", speed);
        tag.setBoolean("oredictMode", oredictMode);
        tag.setBoolean("metaMode", metaMode);
        tag.setBoolean("nbtMode", nbtMode);
        tag.setByte("rsMode", (byte) rsMode.ordinal());
        tag.setBoolean("blacklist", blacklist);
        if (priority != null) {
            tag.setInteger("priority", priority);
        }
        if (count != null) {
            tag.setInteger("count", count);
        }
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            if (ItemStackTools.isValid(filters.get(i))) {
                NBTTagCompound itemTag = new NBTTagCompound();
                filters.get(i).writeToNBT(itemTag);
                tag.setTag("filter" + i, itemTag);
            }
        }
        if (facingOverride != null) {
            tag.setByte("facingOverride", (byte) facingOverride.ordinal());
        }
    }
}
