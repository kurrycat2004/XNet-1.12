package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.logic.ConnectorClientInfo;
import mcjty.xnet.logic.SidedPos;
import mcjty.xnet.multiblock.ConsumerId;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketConsumersReady extends PacketListFromServer<PacketConsumersReady,ConnectorClientInfo> {

    public PacketConsumersReady() {
    }

    public PacketConsumersReady(BlockPos pos, String command, List<ConnectorClientInfo> list) {
        super(pos, command, list);
    }

    @Override
    protected ConnectorClientInfo createItem(ByteBuf buf) {
        BlockPos pos = NetworkTools.readPos(buf);
        EnumFacing side = EnumFacing.values()[buf.readByte()];
        ConsumerId consumerId = new ConsumerId(buf.readInt());
        return new ConnectorClientInfo(new SidedPos(pos, side), consumerId);
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, ConnectorClientInfo item) {
        NetworkTools.writePos(buf, item.getPos().getPos());
        buf.writeByte(item.getPos().getSide().ordinal());
        buf.writeInt(item.getConsumerId().getId());
    }

    public static class Handler implements IMessageHandler<PacketConsumersReady, IMessage> {
        @Override
        public IMessage onMessage(PacketConsumersReady message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketConsumersReady message, MessageContext ctx) {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(message.pos);
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(ConnectorClientInfo.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
