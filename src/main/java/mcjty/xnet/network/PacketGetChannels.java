package mcjty.xnet.network;

import mcjty.lib.network.ICommandHandler;
import mcjty.lib.network.PacketRequestListFromServer;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.clientinfo.ChannelClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketGetChannels extends PacketRequestListFromServer<ChannelClientInfo, PacketGetChannels, PacketChannelsReady> {

    public PacketGetChannels() {

    }

    public PacketGetChannels(BlockPos pos) {
        super(XNet.MODID, pos, TileEntityController.CMD_GETCHANNELS, TypedMap.EMPTY);
    }

    public static class Handler implements IMessageHandler<PacketGetChannels, IMessage> {
        @Override
        public IMessage onMessage(PacketGetChannels message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetChannels message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            ICommandHandler commandHandler = (ICommandHandler) te;
            List<ChannelClientInfo> list = commandHandler.executeWithResultList(message.command, message.params, Type.create(ChannelClientInfo.class));
            XNetMessages.INSTANCE.sendTo(new PacketChannelsReady(message.pos, TileEntityController.CLIENTCMD_CHANNELSREADY, list), ctx.getServerHandler().player);
        }
    }

}
