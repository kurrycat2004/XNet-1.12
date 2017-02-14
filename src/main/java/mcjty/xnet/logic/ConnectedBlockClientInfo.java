package mcjty.xnet.logic;

import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.xnet.api.keys.SidedPos;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class ConnectedBlockClientInfo {

    /// The position of the block we are connecting too
    @Nonnull
    private final SidedPos pos;
    /// The itemstack representing the block
    @Nonnull private final ItemStack connectedBlock;

    public ConnectedBlockClientInfo(@Nonnull SidedPos pos, @Nonnull ItemStack connectedBlock) {
        this.pos = pos;
        this.connectedBlock = connectedBlock;
    }

    public ConnectedBlockClientInfo(@Nonnull ByteBuf buf) {
        pos = new SidedPos(NetworkTools.readPos(buf), EnumFacing.values()[buf.readByte()]);
        connectedBlock = NetworkTools.readItemStack(buf);
    }

    public void writeToBuf(@Nonnull ByteBuf buf) {
        NetworkTools.writePos(buf, pos.getPos());
        buf.writeByte(pos.getSide().ordinal());
        NetworkTools.writeItemStack(buf, connectedBlock);
    }

    @Nonnull
    public SidedPos getPos() {
        return pos;
    }

    @Nonnull
    public ItemStack getConnectedBlock() {
        return connectedBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectedBlockClientInfo that = (ConnectedBlockClientInfo) o;

        if (!pos.equals(that.pos)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }
}
