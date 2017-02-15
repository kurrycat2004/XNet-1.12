package mcjty.xnet.api.channels;

import mcjty.xnet.api.keys.ConsumerId;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.api.keys.SidedConsumer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface IControllerContext {

    NetworkId getNetworkId();

    @Nullable
    BlockPos findConsumerPosition(@Nonnull ConsumerId consumerId);

    @Nonnull
    Map<SidedConsumer, IConnectorSettings> getConnectors(int channel);
}
