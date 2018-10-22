package mcjty.xnet.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class XNetMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetChannels.Handler.class, PacketGetChannels.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetLocalChannelsRouter.Handler.class, PacketGetLocalChannelsRouter.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetRemoteChannelsRouter.Handler.class, PacketGetRemoteChannelsRouter.class, PacketHandler.nextPacketID(), Side.SERVER);
        net.registerMessage(PacketGetConnectedBlocks.Handler.class, PacketGetConnectedBlocks.class, PacketHandler.nextPacketID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketChannelsReady.Handler.class, PacketChannelsReady.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketLocalChannelsRouterReady.Handler.class, PacketLocalChannelsRouterReady.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketRemoteChannelsRouterReady.Handler.class, PacketRemoteChannelsRouterReady.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketConnectedBlocksReady.Handler.class, PacketConnectedBlocksReady.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketJsonToClipboard.Handler.class, PacketJsonToClipboard.class, PacketHandler.nextPacketID(), Side.CLIENT);
        net.registerMessage(PacketControllerError.Handler.class, PacketControllerError.class, PacketHandler.nextPacketID(), Side.CLIENT);
    }
}
