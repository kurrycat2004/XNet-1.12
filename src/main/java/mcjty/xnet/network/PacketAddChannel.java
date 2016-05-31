package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.api.IXNetController;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.handler.WorldHandler;
import mcjty.xnet.multiblock.XNetGrid;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAddChannel implements IMessage {

    private BlockPos pos;
    private String name;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        name = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        NetworkTools.writeString(buf, name);
    }

    public PacketAddChannel() {
    }

    public PacketAddChannel(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public static class Handler implements IMessageHandler<PacketAddChannel, IMessage> {
        @Override
        public IMessage onMessage(PacketAddChannel message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        public void handle(PacketAddChannel message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            XNetGrid grid = WorldHandler.instance.get(playerEntity.getEntityWorld()).xNetWorldGridRegistry.getPowerTile(message.pos).getCurrentGrid();
            IXNetController controllerTE = grid.getController();
            if (controllerTE != null) {
                //controllerTE.addChannel(message.name); TODO
            }
        }
    }

}
