package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.clientinfo.ControllerChannelClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketLocalChannelsRouterReady extends PacketListFromServer<PacketLocalChannelsRouterReady, ControllerChannelClientInfo> {

    public PacketLocalChannelsRouterReady() {
    }

    public PacketLocalChannelsRouterReady(BlockPos pos, String command, List<ControllerChannelClientInfo> list) {
        super(pos, command, list);
    }

    @Override
    protected ControllerChannelClientInfo createItem(ByteBuf buf) {
        if (buf.readBoolean()) {
            return new ControllerChannelClientInfo(buf);
        } else {
            return null;
        }
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, ControllerChannelClientInfo item) {
        if (item == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            item.writeToNBT(buf);
        }
    }

    public static class Handler implements IMessageHandler<PacketLocalChannelsRouterReady, IMessage> {
        @Override
        public IMessage onMessage(PacketLocalChannelsRouterReady message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketLocalChannelsRouterReady message, MessageContext ctx) {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(message.pos);
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(ControllerChannelClientInfo.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
