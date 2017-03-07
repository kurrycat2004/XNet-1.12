package mcjty.xnet.logic;

import mcjty.lib.varia.WorldTools;
import mcjty.xnet.api.keys.ConsumerId;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.blocks.router.TileEntityRouter;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class LogicTools {

    // Find the controller attached to this connector.
    // @todo Note: WorldBlock.findController() is not optimal yet!
    public static TileEntityController getControllerForConnector(@Nonnull World world, @Nonnull BlockPos connectorPos) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(world).getWorldBlob(world);
        NetworkId networkId = worldBlob.getNetworkAt(connectorPos);
        if (networkId == null) {
            return null;
        }
        BlockPos controllerPos = worldBlob.findController(networkId);
        if (!WorldTools.chunkLoaded(world, connectorPos)) {
            return null;
        }
        assert controllerPos != null;
        TileEntity te = world.getTileEntity(controllerPos);
        if (te instanceof TileEntityController) {
            return (TileEntityController) te;
        } else {
            return null;
        }
    }

    public static Stream<BlockPos> consumers(@Nonnull World world, @Nonnull NetworkId networkId) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(world).getWorldBlob(world);
        return worldBlob.getConsumers(networkId).stream();
    }

    public static Stream<BlockPos> connectors(@Nonnull World world, @Nonnull BlockPos pos) {
        return new ConnectorIterator(world, pos, false).stream();
    }

    public static Stream<BlockPos> advancedConnectors(@Nonnull World world, @Nonnull BlockPos pos) {
        return new ConnectorIterator(world, pos, true).stream();
    }

    public static Stream<TileEntityRouter> routers(@Nonnull World world, @Nonnull BlockPos pos) {
        return new RouterIterator(world, pos).stream();
    }

    public static Stream<BlockPos> connectedBlocks(@Nonnull World world, @Nonnull NetworkId networkId, @Nonnull Set<SidedConsumer> consumers) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(world).getWorldBlob(world);
        return consumers.stream()
                .map(sidedConsumer -> {
                    BlockPos consumerPos = findConsumerPosition(networkId, worldBlob, sidedConsumer.getConsumerId());
                    if (consumerPos != null) {
                        return consumerPos.offset(sidedConsumer.getSide());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Nullable
    public static BlockPos findConsumerPosition(@Nonnull NetworkId networkId, @Nonnull WorldBlob worldBlob, @Nonnull ConsumerId consumerId) {
        Set<BlockPos> consumers = worldBlob.getConsumers(networkId);
        for (BlockPos pos : consumers) {
            ConsumerId c = worldBlob.getConsumerAt(pos);
            if (consumerId.equals(c)) {
                return pos;
            }
        }
        return null;
    }


}
