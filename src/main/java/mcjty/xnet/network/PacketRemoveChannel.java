package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.api.IXNetController;
import mcjty.xnet.handler.WorldHandler;
import mcjty.xnet.multiblock.XNetGrid;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRemoveChannel implements IMessage {

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

    public PacketRemoveChannel() {
    }

    public PacketRemoveChannel(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public static class Handler implements IMessageHandler<PacketRemoveChannel, IMessage> {
        @Override
        public IMessage onMessage(PacketRemoveChannel message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        public void handle(PacketRemoveChannel message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            XNetGrid grid = WorldHandler.instance.get(playerEntity.getEntityWorld()).xNetWorldGridRegistry.getPowerTile(message.pos).getCurrentGrid();
            IXNetController controller = grid.getController();
            if (controller != null) {
                //controller.removeChannel(message.name); //TODO
            }
        }
    }

}
