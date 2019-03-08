package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.IClientCommandHandler;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.thirteen.Context;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.Logging;
import mcjty.xnet.XNet;
import mcjty.xnet.clientinfo.ChannelClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Supplier;

public class PacketChannelsReady extends PacketListFromServer<PacketChannelsReady, ChannelClientInfo> {

    public PacketChannelsReady() {
    }

    public PacketChannelsReady(ByteBuf buf) {
        fromBytes(buf);
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

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = XNet.proxy.getClientWorld().getTileEntity(pos);
            IClientCommandHandler clientCommandHandler = (IClientCommandHandler) te;
            if (!clientCommandHandler.receiveListFromServer(command, list, Type.create(ChannelClientInfo.class))) {
                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
