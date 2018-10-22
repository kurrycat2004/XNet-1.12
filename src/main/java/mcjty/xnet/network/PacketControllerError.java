package mcjty.xnet.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.controller.gui.GuiController;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketControllerError implements IMessage {

    private String error;

    @Override
    public void fromBytes(ByteBuf buf) {
        error = NetworkTools.readStringUTF8(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeStringUTF8(buf, error);
    }

    public PacketControllerError() {
    }

    public PacketControllerError(String error) {
        this.error = error;
    }

    public static class Handler implements IMessageHandler<PacketControllerError, IMessage> {
        @Override
        public IMessage onMessage(PacketControllerError message, MessageContext ctx) {
            XNet.proxy.addScheduledTaskClient(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketControllerError message, MessageContext ctx) {
            GuiController.showError(message.error);
        }
    }
}
