package mcjty.xnet.apiimpl.energy;

import cofh.api.energy.IEnergyHandler;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.WorldTools;
import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.cables.ConnectorTileEntity;
import mcjty.xnet.blocks.controller.gui.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnergyChannelSettings implements IChannelSettings {

    public static final String TAG_MODE = "mode";

    enum ChannelMode {
        PRIORITY,
        DISTRIBUTE
    }

    private ChannelMode channelMode = ChannelMode.DISTRIBUTE;

    // Cache data
    private List<Pair<SidedConsumer, EnergyConnectorSettings>> energyExtractors = null;
    private List<Pair<SidedConsumer, EnergyConnectorSettings>> energyConsumers = null;

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
    public int getColors() {
        return 0;
    }

    @Override
    public void tick(int channel, IControllerContext context) {
        updateCache(channel, context);

        World world = context.getControllerWorld();

        // First find out how much energy we have to distribute in total
        int totalToDistribute = 0;
        List<Pair<ConnectorTileEntity, Integer>> energyProducers = new ArrayList<>();
        for (Pair<SidedConsumer, EnergyConnectorSettings> entry : energyExtractors) {
            BlockPos consumerPosition = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (consumerPosition != null) {

                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = consumerPosition.offset(side);
                if (!WorldTools.chunkLoaded(world, pos)) {
                    continue;
                }

                TileEntity te = world.getTileEntity(pos);
                // @todo report error somewhere?
                if (isEnergyTE(te, side.getOpposite())) {
                    EnergyConnectorSettings settings = entry.getValue();
                    ConnectorTileEntity connectorTE = (ConnectorTileEntity) world.getTileEntity(consumerPosition);

                    RSMode rsMode = settings.getRsMode();
                    if (rsMode != RSMode.IGNORED) {
                        if ((rsMode == RSMode.ON) != (connectorTE.getPowerLevel() > 0)) {
                            continue;
                        }
                    }
                    if (!context.matchColor(settings.getColorsMask())) {
                        continue;
                    }

                    Integer count = settings.getMinmax();
                    if (count != null) {
                        int level = getEnergyLevel(te, side.getOpposite());
                        if (level < count) {
                            continue;
                        }
                    }

                    Integer rate = settings.getRate();
                    if (rate == null) {
                        rate = 1000000000;
                    }
                    connectorTE.setEnergyInputFrom(side, rate);

                    int tosend = Math.min(rate, connectorTE.getEnergy());
                    if (tosend > 0) {
                        totalToDistribute += tosend;
                        energyProducers.add(Pair.of(connectorTE, tosend));
                    }
                }
            }
        }

        if (totalToDistribute <= 0) {
            // Nothing to do
            return;
        }

        int actuallyConsumed = insertEnergy(context, totalToDistribute);
        if (actuallyConsumed <= 0) {
            // Nothing was done
            return;
        }

        // Now we need to actually fetch the energy from the producers
        for (Pair<ConnectorTileEntity, Integer> entry : energyProducers) {
            ConnectorTileEntity connectorTE = entry.getKey();
            int amount = entry.getValue();

            int actuallySpent = Math.min(amount, actuallyConsumed);
            connectorTE.setEnergy(connectorTE.getEnergy() - actuallySpent);
            actuallyConsumed -= actuallySpent;
            if (actuallyConsumed <= 0) {
                break;
            }
        }
    }

    private int insertEnergy(@Nonnull IControllerContext context, int energy) {
        int total = 0;
        World world = context.getControllerWorld();
        for (Pair<SidedConsumer, EnergyConnectorSettings> entry : energyConsumers) {
            EnergyConnectorSettings settings = entry.getValue();
            BlockPos extractorPos = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (extractorPos != null) {
                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = extractorPos.offset(side);
                if (!WorldTools.chunkLoaded(world, pos)) {
                    continue;
                }
                TileEntity te = world.getTileEntity(pos);
                // @todo report error somewhere?
                if (isEnergyTE(te, settings.getFacing())) {

                    RSMode rsMode = settings.getRsMode();
                    if (rsMode != RSMode.IGNORED) {
                        ConnectorTileEntity connector = (ConnectorTileEntity) world.getTileEntity(extractorPos);
                        if ((rsMode == RSMode.ON) != (connector.getPowerLevel() > 0)) {
                            continue;
                        }
                    }
                    if (!context.matchColor(settings.getColorsMask())) {
                        continue;
                    }

                    Integer count = settings.getMinmax();
                    if (count != null) {
                        int level = getEnergyLevel(te, settings.getFacing());
                        if (level >= count) {
                            continue;
                        }
                    }

                    Integer rate = settings.getRate();
                    if (rate == null) {
                        rate = 1000000000;
                    }
                    int totransfer = Math.min(rate, energy);
                    int e = EnergyTools.receiveEnergy(te, settings.getFacing(), totransfer);
                    energy -= e;
                    total += e;
                    if (energy <= 0) {
                        return total;
                    }
                }
            }
        }
        return total;
    }

    public static boolean isEnergyTE(TileEntity te, @Nonnull EnumFacing side) {
        return te instanceof IEnergyHandler || (te != null && te.hasCapability(CapabilityEnergy.ENERGY, side));
    }

    public static int getEnergyLevel(TileEntity tileEntity, @Nonnull EnumFacing side) {
        if (tileEntity instanceof IEnergyHandler) {
            IEnergyHandler handler = (IEnergyHandler) tileEntity;
            return handler.getEnergyStored(EnumFacing.DOWN);
        } else if (tileEntity != null && tileEntity.hasCapability(CapabilityEnergy.ENERGY, side)) {
            IEnergyStorage energy = tileEntity.getCapability(CapabilityEnergy.ENERGY, side);
            return energy.getEnergyStored();
        } else {
            return 0;
        }
    }



    @Override
    public void cleanCache() {
        energyExtractors = null;
        energyConsumers = null;
    }

    private void updateCache(int channel, IControllerContext context) {
        if (energyExtractors == null) {
            energyExtractors = new ArrayList<>();
            energyConsumers = new ArrayList<>();
            Map<SidedConsumer, IConnectorSettings> connectors = context.getConnectors(channel);
            for (Map.Entry<SidedConsumer, IConnectorSettings> entry : connectors.entrySet()) {
                EnergyConnectorSettings con = (EnergyConnectorSettings) entry.getValue();
                if (con.getEnergyMode() == EnergyConnectorSettings.EnergyMode.EXT) {
                    energyExtractors.add(Pair.of(entry.getKey(), con));
                } else {
                    energyConsumers.add(Pair.of(entry.getKey(), con));
                }
            }
            energyExtractors.sort((o1, o2) -> o2.getRight().getPriority().compareTo(o1.getRight().getPriority()));
            energyConsumers.sort((o1, o2) -> o2.getRight().getPriority().compareTo(o1.getRight().getPriority()));
        }
    }

    @Override
    public boolean isEnabled(String tag) {
        return true;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 11, 80, 11, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    @Override
    public void createGui(IEditorGui gui) {
        gui.nl().choices(TAG_MODE, "Energy distribution mode", channelMode, ChannelMode.values());
    }

    @Override
    public void update(Map<String, Object> data) {
        channelMode = ChannelMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
    }
}
