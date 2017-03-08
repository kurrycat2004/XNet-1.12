package mcjty.xnet.network;

import mcjty.lib.network.CommandHandler;
import mcjty.lib.network.PacketRequestListFromServer;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.router.TileEntityRouter;
import mcjty.xnet.clientinfo.ControllerChannelClientInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketGetLocalChannelsRouter extends PacketRequestListFromServer<ControllerChannelClientInfo, PacketGetLocalChannelsRouter, PacketLocalChannelsRouterReady> {

    public PacketGetLocalChannelsRouter() {

    }

    public PacketGetLocalChannelsRouter(BlockPos pos) {
        super(XNet.MODID, pos, TileEntityRouter.CMD_GETCHANNELS);
    }

    public static class Handler implements IMessageHandler<PacketGetLocalChannelsRouter, IMessage> {
        @Override
        public IMessage onMessage(PacketGetLocalChannelsRouter message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetLocalChannelsRouter message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            CommandHandler commandHandler = (CommandHandler) te;
            List<ControllerChannelClientInfo> list = commandHandler.executeWithResultList(message.command, message.args, Type.create(ControllerChannelClientInfo.class));
            XNetMessages.INSTANCE.sendTo(new PacketLocalChannelsRouterReady(message.pos, TileEntityRouter.CLIENTCMD_CHANNELSREADY, list), ctx.getServerHandler().player);
        }
    }

}
