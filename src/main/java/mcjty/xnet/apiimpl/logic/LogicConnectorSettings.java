package mcjty.xnet.apiimpl.logic;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogicConnectorSettings implements IConnectorSettings {

    public static final String TAG_FACING = "facing";
    public static final String TAG_MODE = "mode";

    public static final int SENSORS = 5;

    enum Color {
        WHITE,
        RED,
        GREEN,
        BLUE,
        YELLOW,
        ORANGE,
        CYAN,
        PURPLE
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
                .choices(TAG_FACING, "Side from which to operate", facingOverride == null ? side : facingOverride, EnumFacing.VALUES);
        for (Sensor sensor : sensors) {
            sensor.createGui(gui);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        String facing = (String) data.get(TAG_FACING);
        facingOverride = facing == null ? null : EnumFacing.byName(facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("facingOverride")) {
            facingOverride = EnumFacing.VALUES[tag.getByte("facingOverride")];
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if (facingOverride != null) {
            tag.setByte("facingOverride", (byte) facingOverride.ordinal());
        }
    }

}
