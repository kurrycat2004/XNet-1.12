package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketConsumersReady extends PacketListFromServer<PacketConsumersReady,PacketGetConsumers.SidedPos> {

    public PacketConsumersReady() {
    }

    public PacketConsumersReady(BlockPos pos, String command, List<PacketGetConsumers.SidedPos> list) {
        super(pos, command, list);
    }

    @Override
    protected PacketGetConsumers.SidedPos createItem(ByteBuf buf) {
        BlockPos pos = NetworkTools.readPos(buf);
        EnumFacing side = EnumFacing.values()[buf.readByte()];
        return new PacketGetConsumers.SidedPos(pos, side);
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, PacketGetConsumers.SidedPos item) {
        NetworkTools.writePos(buf, item.getPos());
        buf.writeByte(item.getSide().ordinal());
    }

    public static class Handler implements IMessageHandler<PacketConsumersReady, IMessage> {
        @Override
        public IMessage onMessage(PacketConsumersReady message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketConsumersReady message, MessageContext ctx) {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(message.pos);
            if(!(te instanceof ClientCommandHandler)) {
                Logging.log("createInventoryReadyPacket: TileEntity is not a ClientCommandHandler!");
                return;
            }
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(PacketGetConsumers.SidedPos.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
