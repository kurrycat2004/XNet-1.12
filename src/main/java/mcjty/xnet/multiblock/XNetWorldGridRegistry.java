package mcjty.xnet.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.grid.capability.AbstractWorldGridHolder;
import mcjty.xnet.XNet;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.PartSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
        return tile.hasCapability(XNetAPI.XNET_CABLE_CAPABILITY, null) || tile.getClass() == TileEntityController.class;
    }

    @Override
    protected XNetTileData generate(TileEntity tile) {
        return new XNetTileData(tile);
    }

    @Override
    protected void add(XNetTileData xNetTileData) {
        System.out.println("XNetWorldGridRegistry.add");
        XNetGrid myGrid;
        for (EnumFacing facing : EnumFacing.VALUES){
            BlockPos pos = xNetTileData.getPos().offset(facing);
            XNetTileData neighbor = getPowerTile(pos);
            if (neighbor != null){
                myGrid = xNetTileData.getCurrentGrid();
                XNetGrid otherGrid = neighbor.getCurrentGrid();
                if (otherGrid != null && myGrid != null && otherGrid == myGrid){
                    continue;
                }
                if (myGrid == null){
                    if (otherGrid == null){
                        otherGrid = new XNetGrid(this);
                        grids.add(otherGrid);
                        otherGrid.addTile(neighbor);
                    }
                    otherGrid.addTile(xNetTileData);
                } else {
                    if (otherGrid != null) {
                        myGrid.merge(otherGrid);
                        otherGrid.invalidate();
                        grids.remove(otherGrid);
                    } else {
                        myGrid.addTile(neighbor);
                    }
                }
            }
        }
        myGrid = xNetTileData.getCurrentGrid();
        if (myGrid == null){
            XNetGrid newGrid = new XNetGrid(this);
            newGrid.addTile(xNetTileData);
            grids.add(newGrid);
        }
        TileEntity tile = xNetTileData.getTile();
        if (tile instanceof IMultipartContainer) {
            myGrid = xNetTileData.getCurrentGrid();
            for (EnumFacing facing : EnumFacing.VALUES) {
                IMultipart multipart = ((IMultipartContainer) tile).getPartInSlot(PartSlot.getFaceSlot(facing));
                if (multipart instanceof ICapabilityProvider && ((ICapabilityProvider) multipart).hasCapability(XNetAPI.XNET_CAPABILITY, facing.getOpposite())) {
                    myGrid.change(xNetTileData, multipart);
                }
            }
        }
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
        xNetTileData.getCurrentGrid().change(xNetTileData, multiPart);
    }

    @Override
    protected void remove(XNetTileData xNetTileData) {
        System.out.println("XNetWorldGridRegistry.remove");
        XNetGrid grid = xNetTileData.getCurrentGrid();
        grid.onRemoved(xNetTileData);
        Set<XNetTileData> tiles = Sets.newHashSet();
        for (BlockPos pos : grid.getAllLocations()){
            if (!pos.equals(xNetTileData.getPos())){
                XNetTileData tile = getPowerTile(pos);
                if (tile != null){
                    tiles.add(tile);
                } else {
                    XNet.logger.error("Null tile for pos: "+pos);
                }
            }
        }
        removeFromRegistry(xNetTileData);
        grid.change(xNetTileData, null);
        grid.invalidate();
        grids.remove(grid);
        for (XNetTileData tile : tiles){
            tile.setGrid(null);
        }
        for (XNetTileData tile : tiles){
            add(tile);
        }
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
        xNetTileData.getCurrentGrid().change(xNetTileData, multiPart);
    }

    @Override
    protected void onChunkUnload(Collection<XNetTileData> unloadingObjects) {
        for (XNetTileData data : unloadingObjects){
            remove(data);
        }
    }

    @Override
    protected void onTick() {
//        System.out.println("Ticking "+grids.size()+" grids");
        for (XNetGrid grid : grids){
            grid.tick();
        }
    }

    /**
     * Gets called when the world unloads, just before it is removed from the registry and made ready for the GC
     */
    @Override
    public void invalidate() {
        grids.clear();
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
