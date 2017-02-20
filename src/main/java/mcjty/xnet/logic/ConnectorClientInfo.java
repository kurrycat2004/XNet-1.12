package mcjty.xnet.logic;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.keys.SidedPos;
import mcjty.xnet.api.keys.ConsumerId;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class ConnectorClientInfo {

    /// The position of the block we are connecting too
    @Nonnull private final SidedPos pos;

    @Nonnull private final ConsumerId consumerId;

    @Nonnull private final IChannelType channelType;
    @Nonnull private final IConnectorSettings connectorSettings;

    private final boolean advanced;


    public ConnectorClientInfo(@Nonnull SidedPos pos, @Nonnull ConsumerId consumerId,
                               @Nonnull IChannelType channelType,
                               @Nonnull IConnectorSettings connectorSettings,
                               boolean advanced) {
        this.pos = pos;
        this.consumerId = consumerId;
        this.channelType = channelType;
        this.connectorSettings = connectorSettings;
        this.advanced = advanced;
    }

    public ConnectorClientInfo(@Nonnull ByteBuf buf) {
        pos = new SidedPos(NetworkTools.readPos(buf), EnumFacing.VALUES[buf.readByte()]);
        consumerId = new ConsumerId(buf.readInt());
        IChannelType t = XNet.xNetApi.findType(NetworkTools.readString(buf));
        if (t == null) {
            throw new RuntimeException("Cannot happen!");
        }
        channelType = t;
        advanced = buf.readBoolean();
        NBTTagCompound tag = NetworkTools.readTag(buf);
        connectorSettings = channelType.createConnector();
        connectorSettings.readFromNBT(tag);
    }

    public void writeToBuf(@Nonnull ByteBuf buf) {
        NetworkTools.writePos(buf, pos.getPos());
        buf.writeByte(pos.getSide().ordinal());
        buf.writeInt(consumerId.getId());
        NetworkTools.writeString(buf, channelType.getID());
        buf.writeBoolean(advanced);
        NBTTagCompound tag = new NBTTagCompound();
        connectorSettings.writeToNBT(tag);
        NetworkTools.writeTag(buf, tag);
    }

    @Nonnull
    public SidedPos getPos() {
        return pos;
    }

    @Nonnull
    public ConsumerId getConsumerId() {
        return consumerId;
    }

    @Nonnull
    public IConnectorSettings getConnectorSettings() {
        return connectorSettings;
    }

    public boolean isAdvanced() {
        return advanced;
    }
}
