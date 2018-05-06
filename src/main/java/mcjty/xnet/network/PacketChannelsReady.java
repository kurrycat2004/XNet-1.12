package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.lib.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.clientinfo.ChannelClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketChannelsReady extends PacketListFromServer<PacketChannelsReady, ChannelClientInfo> {

    public PacketChannelsReady() {
    }

    public PacketChannelsReady(BlockPos pos, String command, List<ChannelClientInfo> list) {
        super(pos, command, list);
    }

    @Override
    protected ChannelClientInfo createItem(ByteBuf buf) {
        if (buf.readBoolean()) {
            return new ChannelClientInfo(buf);
        } else {
            return null;
        }
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, ChannelClientInfo item) {
        if (item == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            item.writeToNBT(buf);
        }
    }

    public static class Handler implements IMessageHandler<PacketChannelsReady, IMessage> {
        @Override
        public IMessage onMessage(PacketChannelsReady message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketChannelsReady message, MessageContext ctx) {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(message.pos);
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(ChannelClientInfo.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
