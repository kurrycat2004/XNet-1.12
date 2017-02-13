package mcjty.xnet.logic;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.multiblock.ConsumerId;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to communicate channels/connectors to the client (GUI)
 */
public class ChannelClientInfo {

    @Nonnull private final IChannelType type;
    @Nonnull private final IChannelSettings channelSettings;

    private final Map<SidedConsumer, ConnectorClientInfo> connectors = new HashMap<>();

    public ChannelClientInfo(@Nonnull IChannelType type, @Nonnull IChannelSettings channelSettings) {
        this.type = type;
        this.channelSettings = channelSettings;
    }

    public ChannelClientInfo(@Nonnull ByteBuf buf) {
        String id = NetworkTools.readString(buf);
        IChannelType t = XNet.xNetApi.findType(id);
        if (t == null) {
            throw new RuntimeException("Bad type: " + id);
        }
        type = t;
        channelSettings = type.createChannel();
        NBTTagCompound tag = NetworkTools.readTag(buf);
        channelSettings.readFromNBT(tag);

        int size = buf.readInt();
        for (int i = 0 ; i < size ; i++) {
            SidedConsumer key = new SidedConsumer(new ConsumerId(buf.readInt()), EnumFacing.values()[buf.readByte()]);
            ConnectorClientInfo info = new ConnectorClientInfo(buf);
            connectors.put(key, info);
        }
    }

    public void writeToNBT(@Nonnull ByteBuf buf) {
        NetworkTools.writeString(buf, type.getID());
        NBTTagCompound tag = new NBTTagCompound();
        channelSettings.writeToNBT(tag);
        NetworkTools.writeTag(buf, tag);
        buf.writeInt(connectors.size());
        for (Map.Entry<SidedConsumer, ConnectorClientInfo> entry : connectors.entrySet()) {
            SidedConsumer key = entry.getKey();
            ConnectorClientInfo info = entry.getValue();

            buf.writeInt(key.getConsumerId().getId());
            buf.writeByte(key.getSide().ordinal());

            info.writeToBuf(buf);
        }
    }

    @Nonnull
    public IChannelType getType() {
        return type;
    }

    @Nonnull
    public IChannelSettings getChannelSettings() {
        return channelSettings;
    }

    @Nonnull
    public Map<SidedConsumer, ConnectorClientInfo> getConnectors() {
        return connectors;
    }
}
