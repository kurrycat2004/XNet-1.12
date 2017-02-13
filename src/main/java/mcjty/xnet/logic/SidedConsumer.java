package mcjty.xnet.logic;

import mcjty.xnet.multiblock.ConsumerId;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class SidedConsumer {

    private final ConsumerId consumerId;
    private final EnumFacing side;

    /**
     * A consumer ID and a side pointing towards the block
     * we are connecting too.
     */
    public SidedConsumer(@Nonnull ConsumerId consumerId, @Nonnull EnumFacing side) {
        this.consumerId = consumerId;
        this.side = side;
    }

    @Nonnull
    public ConsumerId getConsumerId() {
        return consumerId;
    }

    /**
     * Get the side as seen from this consumer of the connector
     * to an adjacent block.
     */
    @Nonnull
    public EnumFacing getSide() {
        return side;
    }
}
