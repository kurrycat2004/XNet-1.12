package mcjty.xnet.logic;

import mcjty.xnet.multiblock.ConsumerId;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class SidedConsumer {

    private final ConsumerId consumerId;
    private final EnumFacing side;

    public SidedConsumer(@Nonnull ConsumerId consumerId, @Nonnull EnumFacing side) {
        this.consumerId = consumerId;
        this.side = side;
    }

    @Nonnull
    public ConsumerId getConsumerId() {
        return consumerId;
    }

    @Nonnull
    public EnumFacing getSide() {
        return side;
    }
}
