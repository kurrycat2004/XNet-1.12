package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketConnectorsReady implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public PacketConnectorsReady() {
    }

    public PacketConnectorsReady(BlockPos pos, String command, List list) {
    }

    public static class Handler implements IMessageHandler<PacketConnectorsReady, IMessage> {
        @Override
        public IMessage onMessage(PacketConnectorsReady message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketConnectorsReady message, MessageContext ctx) {
        }
    }
}
