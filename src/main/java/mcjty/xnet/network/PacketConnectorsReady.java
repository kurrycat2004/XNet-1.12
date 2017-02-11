package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.logic.SidedConsumer;
import mcjty.xnet.logic.SidedPos;
import mcjty.xnet.multiblock.ConsumerId;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketConnectorsReady extends PacketListFromServer<PacketConnectorsReady, SidedConsumer> {

    public PacketConnectorsReady() {
    }

    public PacketConnectorsReady(BlockPos pos, String command, List<SidedConsumer> list) {
        super(pos, command, list);
    }

    @Override
    protected SidedConsumer createItem(ByteBuf buf) {
        ConsumerId consumerId = new ConsumerId(buf.readInt());
        EnumFacing side = EnumFacing.values()[buf.readByte()];
        return new SidedConsumer(consumerId, side);
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, SidedConsumer item) {
        buf.writeInt(item.getConsumerId().getId());
        buf.writeByte(item.getSide().ordinal());
    }

    public static class Handler implements IMessageHandler<PacketConnectorsReady, IMessage> {
        @Override
        public IMessage onMessage(PacketConnectorsReady message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketConnectorsReady message, MessageContext ctx) {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(message.pos);
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(SidedConsumer.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
