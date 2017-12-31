package mcjty.xnet.apiimpl.items;

import com.google.common.collect.ImmutableSet;
import mcjty.lib.varia.ItemStackList;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.helper.AbstractConnectorSettings;
import mcjty.xnet.blocks.controller.gui.GuiController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class ItemConnectorSettings extends AbstractConnectorSettings {

    public static final String TAG_MODE = "mode";
    public static final String TAG_STACK = "stack";
    public static final String TAG_SPEED = "speed";
    public static final String TAG_EXTRACT = "extract";
    public static final String TAG_OREDICT = "od";
    public static final String TAG_NBT = "nbt";
    public static final String TAG_META = "meta";
    public static final String TAG_PRIORITY = "priority";
    public static final String TAG_COUNT = "count";
    public static final String TAG_FILTER = "flt";
    public static final String TAG_BLACKLIST = "blacklist";

    public static final int FILTER_SIZE = 18;

    public enum ItemMode {
        INS,
        EXT
    }

    public enum StackMode {
        SINGLE,
        STACK
    }

    public enum ExtractMode {
        FIRST,
        RND,
        ORDER
    }

    private ItemMode itemMode = ItemMode.INS;
    private ExtractMode extractMode = ExtractMode.FIRST;
    private int speed = 2;
    private StackMode stackMode = StackMode.SINGLE;
    private boolean oredictMode = false;
    private boolean metaMode = false;
    private boolean nbtMode = false;
    private boolean blacklist = false;
    @Nullable private Integer priority = 0;
    @Nullable private Integer count = null;
    private ItemStackList filters = ItemStackList.create(FILTER_SIZE);

    // Cached matcher for items
    private Predicate<ItemStack> matcher = null;

    public ItemMode getItemMode() {
        return itemMode;
    }

    public ItemConnectorSettings(@Nonnull EnumFacing side) {
        super(side);
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
        advanced = gui.isAdvanced();
        String[] speeds;
        if (advanced) {
            speeds = new String[] { "10", "20", "60", "100", "200" };
        } else {
            speeds = new String[] { "20", "60", "100", "200" };
        }

        sideGui(gui);
        colorsGui(gui);
        redstoneGui(gui);
        gui.nl()
                .choices(TAG_MODE, "Insert or extract mode", itemMode, ItemMode.values())
                .choices(TAG_STACK, "Single item or entire stack", stackMode, StackMode.values())
                .choices(TAG_SPEED, "Number of ticks for each operation", Integer.toString(speed * 10), speeds);

        if (itemMode == ItemMode.EXT) {
            gui.choices(TAG_EXTRACT, "Extract mode (first available,|random slot or round robin)", extractMode, ExtractMode.values());
        }

        gui
                .nl()

                .label("Pri").integer(TAG_PRIORITY, "Insertion priority", priority, 36).shift(5)
                .label("#")
                .integer(TAG_COUNT, itemMode == ItemMode.EXT ? "Amount in destination inventory|to keep" : "Max amount in destination|inventory", count, 36)
                .nl()

                .toggleText(TAG_BLACKLIST, "Enable blacklist mode", "BL", blacklist).shift(2)
                .toggleText(TAG_OREDICT, "Ore dictionary matching", "Ore", oredictMode).shift(2)
                .toggleText(TAG_META, "Metadata matching", "Meta", metaMode).shift(2)
                .toggleText(TAG_NBT, "NBT matching", "NBT", nbtMode)
                .nl();
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            gui.ghostSlot(TAG_FILTER + i, filters.get(i));
        }
    }

    public Predicate<ItemStack> getMatcher() {
        if (matcher == null) {
            ItemStackList filterList = ItemStackList.create();
            for (ItemStack stack : filters) {
                if (!stack.isEmpty()) {
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


    public ExtractMode getExtractMode() {
        return extractMode;
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

    private static Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COLOR+"0", TAG_COLOR+"1", TAG_COLOR+"2", TAG_COLOR+"3", TAG_COUNT, TAG_PRIORITY, TAG_OREDICT, TAG_META, TAG_NBT, TAG_BLACKLIST);
    private static Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COLOR+"0", TAG_COLOR+"1", TAG_COLOR+"2", TAG_COLOR+"3", TAG_COUNT, TAG_OREDICT, TAG_META, TAG_NBT, TAG_BLACKLIST, TAG_STACK, TAG_SPEED, TAG_EXTRACT);

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
        super.update(data);
        itemMode = ItemMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        extractMode = ExtractMode.valueOf(((String)data.get(TAG_EXTRACT)).toUpperCase());
        stackMode = StackMode.valueOf(((String)data.get(TAG_STACK)).toUpperCase());
        speed = Integer.parseInt((String) data.get(TAG_SPEED)) / 10;
        if (speed == 0) {
            speed = 2;
        }
        oredictMode = Boolean.TRUE.equals(data.get(TAG_OREDICT));
        metaMode = Boolean.TRUE.equals(data.get(TAG_META));
        nbtMode = Boolean.TRUE.equals(data.get(TAG_NBT));

        blacklist = Boolean.TRUE.equals(data.get(TAG_BLACKLIST));
        priority = (Integer) data.get(TAG_PRIORITY);
        count = (Integer) data.get(TAG_COUNT);
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            filters.set(i, (ItemStack) data.get(TAG_FILTER+i));
        }
        matcher = null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        itemMode = ItemMode.values()[tag.getByte("itemMode")];
        extractMode = ExtractMode.values()[tag.getByte("extractMode")];
        stackMode = StackMode.values()[tag.getByte("stackMode")];
        speed = tag.getInteger("speed");
        if (speed == 0) {
            speed = 2;
        }
        oredictMode = tag.getBoolean("oredictMode");
        metaMode = tag.getBoolean("metaMode");
        nbtMode = tag.getBoolean("nbtMode");
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
                filters.set(i, new ItemStack(itemTag));
            } else {
                filters.set(i, ItemStack.EMPTY);
            }
        }
        matcher = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte("itemMode", (byte) itemMode.ordinal());
        tag.setByte("extractMode", (byte) extractMode.ordinal());
        tag.setByte("stackMode", (byte) stackMode.ordinal());
        tag.setInteger("speed", speed);
        tag.setBoolean("oredictMode", oredictMode);
        tag.setBoolean("metaMode", metaMode);
        tag.setBoolean("nbtMode", nbtMode);
        tag.setBoolean("blacklist", blacklist);
        if (priority != null) {
            tag.setInteger("priority", priority);
        }
        if (count != null) {
            tag.setInteger("count", count);
        }
        for (int i = 0 ; i < FILTER_SIZE ; i++) {
            if (!filters.get(i).isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                filters.get(i).writeToNBT(itemTag);
                tag.setTag("filter" + i, itemTag);
            }
        }
    }
}
