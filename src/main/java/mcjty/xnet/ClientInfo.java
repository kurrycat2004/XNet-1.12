package mcjty.xnet;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class holds information on client-side only which are global to the mod.
 */
public class ClientInfo {
    private BlockPos highlightedBlock = null;
    private long expireHighlight = 0;

    public void hilightBlock(BlockPos c, long expireHilight) {
        highlightedBlock = c;
        this.expireHighlight = expireHilight;
    }

    public BlockPos getHighlightedBlock() {
        return highlightedBlock;
    }

    public long getExpireHighlight() {
        return expireHighlight;
    }

    @SideOnly(Side.CLIENT)
    public static World getWorld() {
        return Minecraft.getMinecraft().world;
    }
}
