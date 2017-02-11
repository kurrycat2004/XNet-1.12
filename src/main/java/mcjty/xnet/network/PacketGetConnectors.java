package mcjty.xnet.network;

import mcjty.lib.network.CommandHandler;
import mcjty.lib.network.PacketRequestListFromServer;
import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.logic.SidedConsumer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketGetConnectors extends PacketRequestListFromServer<SidedConsumer, PacketGetConnectors, PacketConnectorsReady> {

    public PacketGetConnectors() {

    }

    public PacketGetConnectors(BlockPos pos) {
        super(XNet.MODID, pos, TileEntityController.CMD_GETCONNECTORINFO);
    }

    public static class Handler implements IMessageHandler<PacketGetConnectors, IMessage> {
        @Override
        public IMessage onMessage(PacketGetConnectors message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketGetConnectors message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            CommandHandler commandHandler = (CommandHandler) te;
            List<SidedConsumer> list = commandHandler.executeWithResultList(message.command, message.args, Type.create(SidedConsumer.class));
            XNetMessages.INSTANCE.sendTo(new PacketConnectorsReady(message.pos, TileEntityController.CLIENTCMD_CONNECTORSREADY, list), ctx.getServerHandler().player);
        }
    }

}
