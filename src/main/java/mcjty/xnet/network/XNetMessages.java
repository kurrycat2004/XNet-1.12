package mcjty.xnet.network;

import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class XNetMessages {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerNetworkMessages(SimpleNetworkWrapper net) {
        INSTANCE = net;

        // Server side
        net.registerMessage(PacketGetChannels.Handler.class, PacketGetChannels.class, PacketHandler.nextID(), Side.SERVER);
        net.registerMessage(PacketGetConnectedBlocks.Handler.class, PacketGetConnectedBlocks.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
        net.registerMessage(PacketChannelsReady.Handler.class, PacketChannelsReady.class, PacketHandler.nextID(), Side.CLIENT);
        net.registerMessage(PacketConnectedBlocksReady.Handler.class, PacketConnectedBlocksReady.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
