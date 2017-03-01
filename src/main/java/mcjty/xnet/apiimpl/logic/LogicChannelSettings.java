package mcjty.xnet.apiimpl.logic;

import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.controller.gui.GuiController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogicChannelSettings implements IChannelSettings {

    private int delay = 0;
    private int colors = 0;     // Colors for this channel
    private List<Pair<SidedConsumer, LogicConnectorSettings>> sensors = null;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        delay = tag.getInteger("delay");
        colors = tag.getInteger("colors");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("delay", delay);
        tag.setInteger("colors", colors);
    }

    @Override
    public int getColors() {
        return colors;




        // @todo calculate bitmask of colors!



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

        colors = 0;
        for (Pair<SidedConsumer, LogicConnectorSettings> entry : sensors) {
            LogicConnectorSettings settings = entry.getValue();
            if (d % settings.getSpeed() != 0) {
                continue;
            }
            BlockPos extractorPos = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (extractorPos != null) {
                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = extractorPos.offset(side);
                TileEntity te = context.getControllerWorld().getTileEntity(pos);

                for (Sensor sensor : settings.getSensors()) {
                    if (sensor.test(te, context.getControllerWorld(), pos, settings)) {
                        colors |= 1 << sensor.getOutputColor().ordinal();
                    }
                }
            }
        }

    }

    private void updateCache(int channel, IControllerContext context) {
        if (sensors == null) {
            sensors = new ArrayList<>();
            Map<SidedConsumer, IConnectorSettings> connectors = context.getConnectors(channel);
            for (Map.Entry<SidedConsumer, IConnectorSettings> entry : connectors.entrySet()) {
                LogicConnectorSettings con = (LogicConnectorSettings) entry.getValue();
                sensors.add(Pair.of(entry.getKey(), con));
            }
        }
    }

    @Override
    public void cleanCache() {
        sensors = null;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 11, 90, 11, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    @Override
    public boolean isEnabled(String tag) {
        return true;
    }

    @Override
    public void createGui(IEditorGui gui) {

    }

    @Override
    public void update(Map<String, Object> data) {

    }
}
