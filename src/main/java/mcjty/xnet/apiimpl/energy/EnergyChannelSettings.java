package mcjty.xnet.apiimpl.energy;

import cofh.api.energy.IEnergyReceiver;
import mcjty.lib.varia.EnergyTools;
import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.cables.ConnectorTileEntity;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EnergyChannelSettings implements IChannelSettings {

    // Cache data
    private Map<SidedConsumer, EnergyConnectorSettings> energyExtractors = null;
    private Map<SidedConsumer, EnergyConnectorSettings> energyConsumers = null;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
    }

    @Override
    public void tick(int channel, IControllerContext context) {
        updateCache(channel, context);
        // @todo optimize
        for (Map.Entry<SidedConsumer, EnergyConnectorSettings> entry : energyExtractors.entrySet()) {
            BlockPos consumerPosition = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (consumerPosition != null) {
                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = consumerPosition.offset(side);
                TileEntity te = context.getControllerWorld().getTileEntity(pos);
                // @todo report error somewhere?
                if (EnergyTools.isEnergyTE(te)) {
                    ConnectorTileEntity connectorTE = (ConnectorTileEntity) context.getControllerWorld().getTileEntity(consumerPosition);
                    connectorTE.setEnergyInputFrom(side, 1000);
                    int tosend = Math.max(1000, connectorTE.getEnergy());
                    if (tosend > 0) {
                        int actuallysent = insertEnergy(context, tosend);
                        connectorTE.setEnergy(connectorTE.getEnergy() - actuallysent);
                    }
                }
            }
        }
    }

    private int insertEnergy(@Nonnull IControllerContext context, int energy) {
        int total = energy;
        for (Map.Entry<SidedConsumer, EnergyConnectorSettings> entry : energyConsumers.entrySet()) {
            EnergyConnectorSettings settings = entry.getValue();
            BlockPos consumerPosition = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (consumerPosition != null) {
                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = consumerPosition.offset(side);
                TileEntity te = context.getControllerWorld().getTileEntity(pos);
                // @todo report error somewhere?
                if (EnergyTools.isEnergyTE(te)) {
                    energy -= EnergyTools.receiveEnergy(te, side.getOpposite(), energy);
                    if (energy <= 0) {
                        return total;
                    }
                }
            }
        }
        return total - energy;
    }

    @Override
    public void cleanCache() {
        energyExtractors = null;
        energyConsumers = null;
    }

    private void updateCache(int channel, IControllerContext context) {
        if (energyExtractors == null) {
            energyExtractors = new HashMap<>();
            energyConsumers = new HashMap<>();
            Map<SidedConsumer, IConnectorSettings> connectors = context.getConnectors(channel);
            for (Map.Entry<SidedConsumer, IConnectorSettings> entry : connectors.entrySet()) {
                EnergyConnectorSettings con = (EnergyConnectorSettings) entry.getValue();
                if (con.getEnergyMode() == EnergyConnectorSettings.EnergyMode.EXTRACT) {
                    energyExtractors.put(entry.getKey(), con);
                } else {
                    energyConsumers.put(entry.getKey(), con);
                }
            }
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
    }

    @Override
    public void update(Map<String, Object> data) {
    }
}
