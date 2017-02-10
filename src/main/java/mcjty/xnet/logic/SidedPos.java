package mcjty.xnet.logic;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class SidedPos {
    private final BlockPos pos;
    private final EnumFacing side;

    public SidedPos(@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
        this.pos = pos;
        this.side = side;
    }

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    @Nonnull
    public EnumFacing getSide() {
        return side;
    }
}
