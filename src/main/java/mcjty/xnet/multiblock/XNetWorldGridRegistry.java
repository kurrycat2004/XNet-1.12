package mcjty.xnet.multiblock;

import com.google.common.collect.Lists;
import elec332.core.grid.capability.AbstractWorldGridHolder;
import mcjty.xnet.api.XNetAPI;
import mcmultipart.multipart.IMultipart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Elec332 on 6-3-2016.
 */
public class XNetWorldGridRegistry extends AbstractWorldGridHolder<XNetTileData, XNetSurroundings> {

    public XNetWorldGridRegistry(World world) {
        super(world);
        this.grids = Lists.newArrayList();
    }

    private List<XNetGrid> grids;

    @Override
    protected boolean isValidTile(TileEntity tile) {
        return tile.hasCapability(XNetAPI.XNET_CAPABILITY, null);
    }

    @Override
    protected XNetTileData generate(TileEntity tile) {
        return new XNetTileData(tile);
    }

    @Override
    protected void add(XNetTileData xNetTileData) {
        System.out.println("XNetWorldGridRegistry.add");
    }

    /**
     * Called when a MultiPart gets added to an already registered tile.
     *
     * @param xNetTileData The tile-reference object
     * @param multiPart    The MultiPart that was added, could be an insignificant one
     */
    @Override
    protected void onExtraMultiPartAdded(XNetTileData xNetTileData, IMultipart multiPart) {
        System.out.println("XNetWorldGridRegistry.onExtraMultiPartAdded");
    }

    @Override
    protected void remove(XNetTileData xNetTileData) {

    }

    /**
     * Called when a MultiPart gets removed from an tile that is still valid.
     *
     * @param xNetTileData The tile-reference object
     * @param multiPart    The MultiPart that was removed, could be an insignificant one
     */
    @Override
    protected void onMultiPartRemoved(XNetTileData xNetTileData, IMultipart multiPart) {
        System.out.println("XNetWorldGridRegistry.onMultiPartRemoved");
    }

    @Override
    protected void onChunkUnload(Collection<XNetTileData> unloadingObjects) {

    }

    @Override
    protected void onTick() {
        for (XNetGrid grid : grids){
            grid.tick();
        }
    }

    /**
     * Gets called when the world unloads, just before it is removed from the registry and made ready for the GC
     */
    @Override
    public void invalidate() {

    }

    /**
     * Generates surrounding data for the given position
     *
     * @param world  The world
     * @param pos    The position of which the surroundings hav to be checked
     * @param facing The facing that has to be checked
     * @return The generated data about the surroundings
     */
    @Override
    public XNetSurroundings generateFor(World world, BlockPos pos, EnumFacing facing) {
        return new XNetSurroundings(pos, world, facing);
    }

    @Override
    public boolean equal(XNetSurroundings o1, XNetSurroundings o2) {
        return o1 != null && o1.areEqual(o2);
    }

    @Override
    public void onSurroundingsChanged(BlockPos pos, EnumFacing side, @Nullable XNetSurroundings oldSurroundings, @Nullable XNetSurroundings newSurroundings, boolean blockChange) {

    }

}
