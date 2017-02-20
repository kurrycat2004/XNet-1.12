package mcjty.xnet.apiimpl.energy;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EnergyConnectorSettings implements IConnectorSettings {

    public static final String TAG_MODE = "mode";
    public static final String TAG_RS = "rs";
    public static final String TAG_RATE = "rate";
    public static final String TAG_MINMAX = "minmax";
    public static final String TAG_PRIORITY = "priority";

    enum EnergyMode {
        INS,
        EXT
    }

    private EnergyMode energyMode = EnergyMode.INS;
    private RSMode rsMode = RSMode.IGNORED;

    @Nullable private Integer priority = 0;
    @Nullable private Integer rate = null;
    @Nullable private Integer minmax = null;

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }

    @Override
    public boolean supportsGhostSlots() {
        return false;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        switch (energyMode) {
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
                .choices(TAG_MODE, "Insert or extract mode", energyMode, EnergyMode.values())
                .shift(10)
                .redstoneMode(TAG_RS, rsMode).nl()

                .label("Pri").integer(TAG_PRIORITY, "Insertion priority", priority).nl()

                .label("Rate")
                .integer(TAG_RATE, energyMode == EnergyMode.EXT ? "Max energy extraction rate" : "Max energy insertion rate", rate)
                .shift(10)
                .label(energyMode == EnergyMode.EXT ? "Min" : "Max")
                .integer(TAG_MINMAX, energyMode == EnergyMode.EXT ? "Disable extraction if energy is too low" : "Disable insertion if energy is too high", minmax);
    }

    private static Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_RATE, TAG_MINMAX, TAG_PRIORITY);
    private static Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_RATE, TAG_MINMAX, TAG_PRIORITY);

    @Override
    public boolean isEnabled(String tag) {
        if (energyMode == EnergyMode.INS) {
            return INSERT_TAGS.contains(tag);
        } else {
            return EXTRACT_TAGS.contains(tag);
        }
    }

    @Nonnull
    public Integer getPriority() {
        return priority == null ? 0 : priority;
    }

    @Nullable
    public Integer getRate() {
        return rate;
    }

    @Nullable
    public Integer getMinmax() {
        return minmax;
    }

    public RSMode getRsMode() {
        return rsMode;
    }

    @Override
    public void update(Map<String, Object> data) {
        energyMode = EnergyMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        rsMode = RSMode.valueOf(((String)data.get(TAG_RS)).toUpperCase());
        rate = (Integer) data.get(TAG_RATE);
        minmax = (Integer) data.get(TAG_MINMAX);
        priority = (Integer) data.get(TAG_PRIORITY);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        energyMode = EnergyMode.values()[tag.getByte("itemMode")];
        rsMode = RSMode.values()[tag.getByte("rsMode")];
        if (tag.hasKey("priority")) {
            priority = tag.getInteger("priority");
        } else {
            priority = null;
        }
        if (tag.hasKey("rate")) {
            rate = tag.getInteger("rate");
        } else {
            rate = null;
        }
        if (tag.hasKey("minmax")) {
            minmax = tag.getInteger("minmax");
        } else {
            minmax = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("itemMode", (byte) energyMode.ordinal());
        tag.setByte("rsMode", (byte) rsMode.ordinal());
        if (priority != null) {
            tag.setInteger("priority", priority);
        }
        if (rate != null) {
            tag.setInteger("rate", rate);
        }
        if (minmax != null) {
            tag.setInteger("minmax", minmax);
        }
    }
}
