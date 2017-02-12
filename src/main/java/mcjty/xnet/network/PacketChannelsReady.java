package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.ClientCommandHandler;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.PacketListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.logic.ChannelInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketChannelsReady extends PacketListFromServer<PacketChannelsReady, ChannelInfo> {

    public PacketChannelsReady() {
    }

    public PacketChannelsReady(BlockPos pos, String command, List<ChannelInfo> list) {
        super(pos, command, list);
    }

    @Override
    protected ChannelInfo createItem(ByteBuf buf) {
        if (buf.readBoolean()) {
            NBTTagCompound tag = NetworkTools.readTag(buf);
            String typeId = tag.getString("type");
            IChannelType type = XNet.xNetApi.findType(typeId);
            ChannelInfo info = new ChannelInfo(type);
            info.readFromNBT(tag);
            return info;
        } else {
            return null;
        }
    }

    @Override
    protected void writeItemToBuf(ByteBuf buf, ChannelInfo item) {
        if (item == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("type", item.getType().getID());
            item.writeToNBT(tag);
            NetworkTools.writeTag(buf, tag);
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
            if (!clientCommandHandler.execute(message.command, message.list, Type.create(ChannelInfo.class))) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
