package mcjty.xnet.logic;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.api.keys.ConsumerId;
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
    private final boolean enabled;

    private final Map<SidedConsumer, ConnectorClientInfo> connectors = new HashMap<>();

    public ChannelClientInfo(@Nonnull IChannelType type, @Nonnull IChannelSettings channelSettings, boolean enabled) {
        this.type = type;
        this.channelSettings = channelSettings;
        this.enabled = enabled;
    }

    public ChannelClientInfo(@Nonnull ByteBuf buf) {
        enabled = buf.readBoolean();
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
            SidedConsumer key = new SidedConsumer(new ConsumerId(buf.readInt()), EnumFacing.VALUES[buf.readByte()]);
            ConnectorClientInfo info = new ConnectorClientInfo(buf);
            connectors.put(key, info);
        }
    }

    public void writeToNBT(@Nonnull ByteBuf buf) {
        buf.writeBoolean(enabled);
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

    public boolean isEnabled() {
        return enabled;
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
