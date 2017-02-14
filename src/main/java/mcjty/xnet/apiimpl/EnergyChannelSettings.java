package mcjty.xnet.apiimpl;

import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IEditorGui;
import mcjty.xnet.api.channels.IndicatorIcon;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Map;

public class EnergyChannelSettings implements IChannelSettings {

    @Override
    public void readFromNBT(NBTTagCompound tag) {
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
    }

    @Override
    public boolean isEnabled(String tag) {
        return true;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 10, 80, 10, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    @Override
    public void createGui(IEditorGui gui) {
    }

    @Override
    public void update(Map<String, Object> data) {
    }
}
