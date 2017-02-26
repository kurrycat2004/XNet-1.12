package mcjty.xnet.apiimpl.fluids;

import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FluidChannelSettings implements IChannelSettings {

    public static final String TAG_MODE = "mode";

    enum ChannelMode {
        PRIORITY,
        DISTRIBUTE
    }

    private ChannelMode channelMode = ChannelMode.DISTRIBUTE;

    // Cache data
    private List<Pair<SidedConsumer, FluidConnectorSettings>> fluidExtractors = null;
    private List<Pair<SidedConsumer, FluidConnectorSettings>> fluidConsumers = null;

    public ChannelMode getChannelMode() {
        return channelMode;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        channelMode = ChannelMode.values()[tag.getByte("mode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("mode", (byte) channelMode.ordinal());
    }

    @Override
    public void tick(int channel, IControllerContext context) {
        updateCache(channel, context);

    }

    private static boolean isFluidTE(TileEntity te, @Nonnull EnumFacing side) {
        return te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }


    @Override
    public void cleanCache() {
        fluidExtractors = null;
        fluidConsumers = null;
    }

    private void updateCache(int channel, IControllerContext context) {
        if (fluidExtractors == null) {
            fluidExtractors = new ArrayList<>();
            fluidConsumers = new ArrayList<>();
            Map<SidedConsumer, IConnectorSettings> connectors = context.getConnectors(channel);
            for (Map.Entry<SidedConsumer, IConnectorSettings> entry : connectors.entrySet()) {
                FluidConnectorSettings con = (FluidConnectorSettings) entry.getValue();
                if (con.getFluidMode() == FluidConnectorSettings.FluidMode.EXT) {
                    fluidExtractors.add(Pair.of(entry.getKey(), con));
                } else {
                    fluidConsumers.add(Pair.of(entry.getKey(), con));
                }
            }
            fluidExtractors.sort((o1, o2) -> o2.getRight().getPriority().compareTo(o1.getRight().getPriority()));
            fluidConsumers.sort((o1, o2) -> o2.getRight().getPriority().compareTo(o1.getRight().getPriority()));
        }
    }

    @Override
    public boolean isEnabled(String tag) {
        return true;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 22, 80, 11, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    @Override
    public void createGui(IEditorGui gui) {
        gui.nl().choices(TAG_MODE, "Fluid distribution mode", channelMode, ChannelMode.values());
    }

    @Override
    public void update(Map<String, Object> data) {
        channelMode = ChannelMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
    }
}
