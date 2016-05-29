package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.ControllerTE;
import mcjty.xnet.channel.Channel;
import mcjty.xnet.handler.WorldHandler;
import mcjty.xnet.multiblock.XNetGrid;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class PacketGetChannels implements IMessage {

    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetChannels() {
    }

    public PacketGetChannels(BlockPos pos) {
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<PacketGetChannels, IMessage> {
        @Override
        public IMessage onMessage(PacketGetChannels message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        public void handle(PacketGetChannels message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            XNetGrid grid = WorldHandler.instance.get(playerEntity.getEntityWorld()).xNetWorldGridRegistry.getPowerTile(message.pos).getCurrentGrid();
            ControllerTE controllerTE = grid.getController(playerEntity.getEntityWorld());
            if (controllerTE != null) {
                List<String> channelInfo = new ArrayList<>();
                List<Channel> channels = controllerTE.getChannels();
                for (Channel channel : channels) {
                    channelInfo.add(channel.getName());
                }
                XNet.networkHandler.getNetworkWrapper().sendTo(new PacketChannelsReady(message.pos, channelInfo), playerEntity);
            }
        }
    }

}
