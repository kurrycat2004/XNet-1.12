package mcjty.xnet.apiimpl.energy;

import com.google.common.collect.ImmutableSet;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EnergyConnectorSettings implements IConnectorSettings {

    public static final String TAG_MODE = "mode";
    public static final String TAG_RS = "rs";

    enum EnergyMode {
        INSERT,
        EXTRACT
    }

    private EnergyMode energyMode = EnergyMode.INSERT;
    private RSMode rsMode = RSMode.IGNORED;

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
                .choices(TAG_MODE, "Insert or extract mode", energyMode, EnergyMode.values())
                .shift(10)
                .redstoneMode(TAG_RS, rsMode);
    }

    private static Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS);
    private static Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS);

    @Override
    public boolean isEnabled(String tag) {
        if (energyMode == EnergyMode.INSERT) {
            return INSERT_TAGS.contains(tag);
        } else {
            return EXTRACT_TAGS.contains(tag);
        }
    }

    @Override
    public void update(Map<String, Object> data) {
        energyMode = EnergyMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        rsMode = RSMode.valueOf(((String)data.get(TAG_RS)).toUpperCase());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        energyMode = EnergyMode.values()[tag.getByte("itemMode")];
        rsMode = RSMode.values()[tag.getByte("rsMode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("itemMode", (byte) energyMode.ordinal());
        tag.setByte("rsMode", (byte) rsMode.ordinal());
    }
}
