package mcjty.xnet.logic;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class ControllerChannelClientInfo {
    // Position of the controller
    @Nonnull private final String channelName;
    @Nonnull private final BlockPos pos;
    @Nonnull private final IChannelType channelType;
    private final int index;        // Index of the channel within that controller (0 through 7)

    public ControllerChannelClientInfo(@Nonnull String channelName, @Nonnull BlockPos pos, @Nonnull IChannelType channelType, int index) {
        this.channelName = channelName;
        this.pos = pos;
        this.channelType = channelType;
        this.index = index;
    }

    public ControllerChannelClientInfo(@Nonnull ByteBuf buf) {
        channelName = NetworkTools.readStringUTF8(buf);
        String id = NetworkTools.readString(buf);
        IChannelType t = XNet.xNetApi.findType(id);
        if (t == null) {
            throw new RuntimeException("Bad type: " + id);
        }
        channelType = t;
        pos = NetworkTools.readPos(buf);
        index = buf.readInt();
    }

    public void writeToNBT(@Nonnull ByteBuf buf) {
        NetworkTools.writeStringUTF8(buf, channelName);
        NetworkTools.writeString(buf, channelType.getID());
        NetworkTools.writePos(buf, pos);
        buf.writeInt(index);
    }

    @Nonnull
    public String getChannelName() {
        return channelName;
    }

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    @Nonnull
    public IChannelType getChannelType() {
        return channelType;
    }

    public int getIndex() {
        return index;
    }
}
