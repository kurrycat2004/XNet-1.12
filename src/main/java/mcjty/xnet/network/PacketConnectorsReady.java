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

public class PacketConnectorsReady implements IMessage {

    private BlockPos pos;
    private List<BlockPos> devices;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        int s = buf.readInt();
        devices = new ArrayList<>(s);
        for (int i = 0 ; i < s ; i++) {
            devices.add(NetworkTools.readPos(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        buf.writeInt(devices.size());
        for (BlockPos pos : devices) {
            NetworkTools.writePos(buf, pos);
        }
    }

    public PacketConnectorsReady() {
    }

    public PacketConnectorsReady(BlockPos pos, List<BlockPos> list) {
        this.pos = pos;
        this.devices = list;
    }

    public static class Handler implements IMessageHandler<PacketConnectorsReady, IMessage> {
        @Override
        public IMessage onMessage(PacketConnectorsReady message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketConnectorsReady message, MessageContext ctx) {
            GuiTerminal.devicesFromServer = new ArrayList<>(message.devices);
        }
    }
}
