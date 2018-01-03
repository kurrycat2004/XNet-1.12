package mcjty.xnet.api.channels;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

/**
 * Implement this on your block if you want to be connectable to an XNet connector
 */
public interface IConnectable {

    /**
     * Return true if a connector can connect to this block. Warning! This has to work
     * both client and server side!
     * @param access
     * @param connectorPos the position of the connector (not your block!)
     * @param blockPos the position of your block
     * @param facting the direction (as seen from the connector) towards your block
     */
    ConnectResult canConnect(@Nonnull IBlockAccess access, @Nonnull BlockPos connectorPos, @Nonnull BlockPos blockPos, @Nonnull EnumFacing facting);

    enum ConnectResult {
        NO,                 // No connection possible. Don't try further
        YES,                // Connection ok
        DEFAULT             // Don't know. Let XNet decide
    }
}
