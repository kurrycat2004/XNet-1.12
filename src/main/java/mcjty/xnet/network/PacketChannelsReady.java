package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.terminal.GuiTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class PacketChannelsReady implements IMessage {

    private BlockPos pos;
    private List<String> channelInfo;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        int s = buf.readInt();
        channelInfo = new ArrayList<>(s);
        for (int i = 0 ; i < s ; i++) {
            channelInfo.add(NetworkTools.readString(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(channelInfo.size());
        for (String name : channelInfo) {
            NetworkTools.writeString(buf, name);
        }
    }

    public PacketChannelsReady() {
    }

    public PacketChannelsReady(BlockPos pos, List<String> list) {
        this.pos = pos;
        this.channelInfo = list;
    }

    public static class Handler implements IMessageHandler<PacketChannelsReady, IMessage> {
        @Override
        public IMessage onMessage(PacketChannelsReady message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketChannelsReady message, MessageContext ctx) {
            GuiTerminal.channelsFromServer = new ArrayList<>(message.channelInfo);
        }
    }
}
