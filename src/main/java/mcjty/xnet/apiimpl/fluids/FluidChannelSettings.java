package mcjty.xnet.apiimpl.fluids;

import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.cables.ConnectorTileEntity;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class FluidChannelSettings implements IChannelSettings {

    public static final String TAG_MODE = "mode";

    enum ChannelMode {
        PRIORITY,
        DISTRIBUTE
    }

    private ChannelMode channelMode = ChannelMode.DISTRIBUTE;
    private int delay = 0;
    private int roundRobinOffset = 0;

    // Cache data
    private Map<SidedConsumer, FluidConnectorSettings> fluidExtractors = null;
    private List<Pair<SidedConsumer, FluidConnectorSettings>> fluidConsumers = null;

    public ChannelMode getChannelMode() {
        return channelMode;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        channelMode = ChannelMode.values()[tag.getByte("mode")];
        delay = tag.getInteger("delay");
        roundRobinOffset = tag.getInteger("offset");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("mode", (byte) channelMode.ordinal());
        tag.setInteger("delay", delay);
        tag.setInteger("offset", roundRobinOffset);
    }

    @Override
    public void tick(int channel, IControllerContext context) {
        delay--;
        if (delay <= 0) {
            delay = 200*6;      // Multiply of the different speeds we have
        }
        if (delay % 10 != 0) {
            return;
        }
        int d = delay/10;

        updateCache(channel, context);
        // @todo optimize
        for (Map.Entry<SidedConsumer, FluidConnectorSettings> entry : fluidExtractors.entrySet()) {
            BlockPos extractorPos = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (extractorPos != null) {
                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = extractorPos.offset(side);
                TileEntity te = context.getControllerWorld().getTileEntity(pos);
                FluidConnectorSettings settings = entry.getValue();
                IFluidHandler handler = getFluidHandlerAt(te, settings.getFacing());
                // @todo report error somewhere?
                if (handler != null) {

                    RSMode rsMode = settings.getRsMode();
                    if (rsMode != RSMode.IGNORED) {
                        ConnectorTileEntity connector = (ConnectorTileEntity) context.getControllerWorld().getTileEntity(extractorPos);
                        if ((rsMode == RSMode.ON) != (connector.getPowerLevel() > 0)) {
                            continue;
                        }
                    }

                    if (d % settings.getSpeed() != 0) {
                        continue;
                    }

//                    Predicate<ItemStack> extractMatcher = settings.getMatcher();

//                    Integer count = settings.getMinmax();
//                    if (count != null) {
//                        int amount = countItems(handler, extractMatcher);
//                        if (amount < count) {
//                            continue;
//                        }
//                    }

                    FluidStack stack = fetchFluid(handler, true, fluidStack -> true, settings.getRate());
                    if (stack != null) {
                        Pair<SidedConsumer, FluidConnectorSettings> inserted = insertFluidSimulate(context, stack);
                        if (inserted != null) {
                            insertFluidReal(context, inserted, fetchFluid(handler, false, fluidStack -> true, settings.getRate()));
                        }
                    }
                }
            }
        }

    }

    private static boolean isFluidTE(TileEntity te, @Nonnull EnumFacing side) {
        return te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }


    @Override
    public void cleanCache() {
        fluidExtractors = null;
        fluidConsumers = null;
    }

    private FluidStack fetchFluid(IFluidHandler handler, boolean simulate, Predicate<FluidStack> matcher, int rate) {
        return handler.drain(rate, !simulate);
    }

    private Pair<SidedConsumer, FluidConnectorSettings> insertFluidSimulate(@Nonnull IControllerContext context, @Nonnull FluidStack stack) {
        for (int j = 0 ; j < fluidConsumers.size() ; j++) {
            int i = (j + roundRobinOffset)  % fluidConsumers.size();
            Pair<SidedConsumer, FluidConnectorSettings> entry = fluidConsumers.get(i);
            FluidConnectorSettings settings = entry.getValue();

            if (true) { //settings.getMatcher().test(stack)) {
                BlockPos consumerPos = context.findConsumerPosition(entry.getKey().getConsumerId());
                if (consumerPos != null) {
                    RSMode rsMode = settings.getRsMode();
                    if (rsMode != RSMode.IGNORED) {
                        ConnectorTileEntity connector = (ConnectorTileEntity) context.getControllerWorld().getTileEntity(consumerPos);
                        if ((rsMode == RSMode.ON) != (connector.getPowerLevel() > 0)) {
                            continue;
                        }
                    }

                    EnumFacing side = entry.getKey().getSide();
                    BlockPos pos = consumerPos.offset(side);
                    TileEntity te = context.getControllerWorld().getTileEntity(pos);
                    IFluidHandler handler = getFluidHandlerAt(te, settings.getFacing());
                    // @todo report error somewhere?
                    if (handler != null) {
                        Integer count = settings.getMinmax();
//                        if (count != null) {
//                            int amount = countItems(handler, settings.getMatcher());
//                            if (amount >= count) {
//                                continue;
//                            }
//                        }

                        int filled = handler.fill(stack, false);
                        if (filled == stack.amount) {
                            return entry;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void insertFluidReal(@Nonnull IControllerContext context, @Nonnull Pair<SidedConsumer, FluidConnectorSettings> entry, @Nonnull FluidStack stack) {
        BlockPos consumerPosition = context.findConsumerPosition(entry.getKey().getConsumerId());
        EnumFacing side = entry.getKey().getSide();
        BlockPos pos = consumerPosition.offset(side);
        TileEntity te = context.getControllerWorld().getTileEntity(pos);
        IFluidHandler handler = getFluidHandlerAt(te, side.getOpposite());
        // @todo check this check
        if (handler.fill(stack, true) != 0) {
            roundRobinOffset = (roundRobinOffset+1) % fluidConsumers.size();
        }
    }



    private void updateCache(int channel, IControllerContext context) {
        if (fluidExtractors == null) {
            fluidExtractors = new HashMap<>();
            fluidConsumers = new ArrayList<>();
            Map<SidedConsumer, IConnectorSettings> connectors = context.getConnectors(channel);
            for (Map.Entry<SidedConsumer, IConnectorSettings> entry : connectors.entrySet()) {
                FluidConnectorSettings con = (FluidConnectorSettings) entry.getValue();
                if (con.getFluidMode() == FluidConnectorSettings.FluidMode.EXT) {
                    fluidExtractors.put(entry.getKey(), con);
                } else {
                    fluidConsumers.add(Pair.of(entry.getKey(), con));
                }
            }
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

    @Nullable
    private IFluidHandler getFluidHandlerAt(@Nullable TileEntity te, EnumFacing intSide) {
        if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, intSide)) {
            IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, intSide);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }
}
