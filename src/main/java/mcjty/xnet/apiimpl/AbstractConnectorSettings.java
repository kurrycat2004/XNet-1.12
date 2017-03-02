package mcjty.xnet.apiimpl;

import mcjty.xnet.api.channels.Color;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.api.gui.IEditorGui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public abstract class AbstractConnectorSettings implements IConnectorSettings {

    public static final String TAG_RS = "rs";
    public static final String TAG_COLOR = "color";
    public static final String TAG_FACING = "facing";

    private RSMode rsMode = RSMode.IGNORED;
    private Color[] colors = new Color[]{Color.OFF, Color.OFF, Color.OFF, Color.OFF};
    private int colorsMask = 0;

    private final boolean advanced;

    @Nonnull
    private final EnumFacing side;
    @Nullable
    private EnumFacing facingOverride = null; // Only available on advanced connectors

    public AbstractConnectorSettings(boolean advanced, @Nonnull EnumFacing side) {
        this.side = side;
        this.advanced = advanced;
    }

    @Nonnull
    public EnumFacing getFacing() {
        return facingOverride == null ? side : facingOverride;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public RSMode getRsMode() {
        return rsMode;
    }

    public Color[] getColors() {
        return colors;
    }

    private void calculateColorsMask() {
        colorsMask = 0;
        for (Color color : colors) {
            if (color != null && color != Color.OFF) {
                colorsMask |= 1 << color.ordinal();
            }
        }
    }

    public int getColorsMask() {
        return colorsMask;
    }

    @Override
    public void update(Map<String, Object> data) {
        rsMode = RSMode.valueOf(((String) data.get(TAG_RS)).toUpperCase());
        colors[0] = Color.colorByValue((Integer) data.get(TAG_COLOR + "0"));
        colors[1] = Color.colorByValue((Integer) data.get(TAG_COLOR + "1"));
        colors[2] = Color.colorByValue((Integer) data.get(TAG_COLOR + "2"));
        colors[3] = Color.colorByValue((Integer) data.get(TAG_COLOR + "3"));
        calculateColorsMask();
        String facing = (String) data.get(TAG_FACING);
        facingOverride = facing == null ? null : EnumFacing.byName(facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        rsMode = RSMode.values()[tag.getByte("rsMode")];
        colors[0] = Color.values()[tag.getByte("color0")];
        colors[1] = Color.values()[tag.getByte("color1")];
        colors[2] = Color.values()[tag.getByte("color2")];
        colors[3] = Color.values()[tag.getByte("color3")];
        calculateColorsMask();
        if (tag.hasKey("facingOverride")) {
            facingOverride = EnumFacing.VALUES[tag.getByte("facingOverride")];
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("rsMode", (byte) rsMode.ordinal());
        tag.setByte("color0", (byte) colors[0].ordinal());
        tag.setByte("color1", (byte) colors[1].ordinal());
        tag.setByte("color2", (byte) colors[2].ordinal());
        tag.setByte("color3", (byte) colors[3].ordinal());
        if (facingOverride != null) {
            tag.setByte("facingOverride", (byte) facingOverride.ordinal());
        }
    }

    protected IEditorGui sideGui(IEditorGui gui) {
        return gui.choices(TAG_FACING, "Side from which to operate", facingOverride == null ? side : facingOverride, EnumFacing.VALUES);
    }

    protected IEditorGui colorsGui(IEditorGui gui) {
        return gui
                .colors(TAG_COLOR + "0", "Enable on color", colors[0].getColor(), Color.COLORS)
                .colors(TAG_COLOR + "1", "Enable on color", colors[1].getColor(), Color.COLORS)
                .colors(TAG_COLOR + "2", "Enable on color", colors[2].getColor(), Color.COLORS)
                .colors(TAG_COLOR + "3", "Enable on color", colors[3].getColor(), Color.COLORS);
    }

    protected IEditorGui redstoneGui(IEditorGui gui) {
        return gui.redstoneMode(TAG_RS, rsMode);
    }
}
