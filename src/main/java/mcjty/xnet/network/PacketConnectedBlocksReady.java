package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.logic.ConnectedBlockClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketConnectedBlocksReady extends PacketListFromServer<PacketConnectedBlocksReady, ConnectedBlockClientInfo> {

    public PacketConnectedBlocksReady() {
    }

    public PacketConnectedBlocksReady(BlockPos pos, String command, List<ConnectedBlockClientInfo> list) {
        super(pos, command, list);
    }

    @Override
    protected ConnectedBlockClientInfo createItem(ByteBuf buf) {
        return new ConnectedBlockClientInfo(buf);
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, ConnectedBlockClientInfo item) {
        item.writeToBuf(buf);
    }

    public static class Handler implements IMessageHandler<PacketConnectedBlocksReady, IMessage> {
        @Override
        public IMessage onMessage(PacketConnectedBlocksReady message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketConnectedBlocksReady message, MessageContext ctx) {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(message.pos);
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(ConnectedBlockClientInfo.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
