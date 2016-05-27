package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
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

public class PacketGetConnectors implements IMessage {

    private BlockPos pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
    }

    public PacketGetConnectors() {
    }

    public PacketGetConnectors(BlockPos pos) {
        this.pos = pos;
    }

    public static class Handler implements IMessageHandler<PacketGetConnectors, IMessage> {
        @Override
        public IMessage onMessage(PacketGetConnectors message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        public void handle(PacketGetConnectors message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            XNetGrid grid = WorldHandler.instance.get(playerEntity.getEntityWorld()).xNetWorldGridRegistry.getPowerTile(message.pos).getCurrentGrid();

            List<BlockPos> devices = new ArrayList<>();
            for (XNetGrid.FacedPosition connector : grid.getAllConnectors()) {
                for (EnumFacing facing : connector.getSides()) {
                    BlockPos connectedDevice = connector.getPos().offset(facing);
                    devices.add(connectedDevice);
                }
            }
            XNet.networkHandler.getNetworkWrapper().sendTo(new PacketConnectorsReady(message.pos, devices), playerEntity);
        }
    }

}
