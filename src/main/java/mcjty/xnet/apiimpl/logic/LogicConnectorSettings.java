package mcjty.xnet.apiimpl.logic;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.blocks.controller.gui.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class LogicConnectorSettings implements IConnectorSettings {

    public static final String TAG_FACING = "facing";
    public static final String TAG_MODE = "mode";

    public static final int SENSORS = 4;

    enum Color {
        BLACK(0x000000),
        WHITE(0xffffff),
        RED(0xff0000),
        GREEN(0x00ff00),
        BLUE(0x0000ff),
        YELLOW(0xffff00),
        CYAN(0x00ffff),
        PURPLE(0xff00ff);

        private final int color;

        Color(int color) {
            this.color = color;
        }

        private static final Map<Integer, Color> COLOR_MAP = new HashMap<>();
        public static final Integer[] COLORS = new Integer[Color.values().length];
        static {
            for (int i = 0 ; i < Color.values().length ; i++) {
                Color col = Color.values()[i];
                COLORS[i] = col.color;
                COLOR_MAP.put(col.color, col);
            }
        }

        public int getColor() {
            return color;
        }

        public static Color colorByValue(int color) {
            return COLOR_MAP.get(color);
        }
    }

    private List<Sensor> sensors = null;


    private final boolean advanced;
    @Nonnull private final EnumFacing side;
    @Nullable private EnumFacing facingOverride = null; // Only available on advanced connectors

    public LogicConnectorSettings(boolean advanced, @Nonnull EnumFacing side) {
        this.advanced = advanced;
        this.side = side;
        sensors = new ArrayList<>(SENSORS);
        for (int i = 0 ; i < SENSORS ; i++) {
            sensors.add(new Sensor());
        }
    }

    @Nonnull
    public EnumFacing getFacing() {
        return facingOverride == null ? side : facingOverride;
    }


    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 26, 70, 13, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    private static Set<String> SENSOR_TAGS = ImmutableSet.of(TAG_MODE);

    @Override
    public boolean isEnabled(String tag) {
        if (tag.equals(TAG_FACING)) {
            return advanced;
        }
        return SENSOR_TAGS.contains(tag);
    }

    @Override
    public void createGui(IEditorGui gui) {
        gui
                .choices(TAG_FACING, "Side from which to operate", facingOverride == null ? side : facingOverride, EnumFacing.VALUES)
                .nl();
        for (Sensor sensor : sensors) {
            sensor.createGui(gui);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        String facing = (String) data.get(TAG_FACING);
        facingOverride = facing == null ? null : EnumFacing.byName(facing);
        int i = 0;
        for (Sensor sensor : sensors) {
            sensor.update(i, data);
            i++;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("facingOverride")) {
            facingOverride = EnumFacing.VALUES[tag.getByte("facingOverride")];
        }
        int i = 0;
        for (Sensor sensor : sensors) {
            sensor.readFromNBT(i, tag);
            i++;
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (facingOverride != null) {
            tag.setByte("facingOverride", (byte) facingOverride.ordinal());
        }
        int i = 0;
        for (Sensor sensor : sensors) {
            sensor.writeToNBT(i, tag);
            i++;
        }
    }

}
