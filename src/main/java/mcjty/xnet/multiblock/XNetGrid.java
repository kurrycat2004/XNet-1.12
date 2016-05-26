package mcjty.xnet.multiblock;

import com.google.common.collect.Sets;
import mcjty.xnet.XNet;
import mcmultipart.multipart.IMultipart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 6-3-2016.
 */
public class XNetGrid {

    public XNetGrid(XNetWorldGridRegistry registry){
        this.worldGridRegistry = registry;
        allLocations = Sets.newHashSet();
        allLocations_ = Collections.unmodifiableSet(allLocations);
        allConnectors = Sets.newHashSet();
    }

    private final Set<BlockPos> allLocations;
    private final Set<BlockPos> allLocations_;
    private final Set<FacedPosition> allConnectors;
    private final XNetWorldGridRegistry worldGridRegistry;

    public void tick(){
        System.out.println("XNetGrid.tick");
        for (BlockPos pos : allLocations) {
            System.out.println("    pos = " + pos);
        }
        for (FacedPosition connector : allConnectors) {
            System.out.println("connector = " + connector);
        }


    }

    public void invalidate(){
    }

    public Set<BlockPos> getAllLocations() {
        return allLocations_;
    }

    protected void merge(XNetGrid grid){
        for (BlockPos pos : grid.getAllLocations()){
            XNetTileData tile = worldGridRegistry.getPowerTile(pos);
            if (tile != null) {
                addTile(tile);
            } else {
                XNet.logger.error("Null tile for pos: " + pos);
            }
            addTile(worldGridRegistry.getPowerTile(pos));
        }
    }

    protected void addTile(XNetTileData tile){
        allLocations.add(tile.getPos());
        tile.setGrid(this);
    }

    protected void onRemoved(XNetTileData tile) {
    }

    void change(XNetTileData tile, IMultipart multipart){
    }

    private class FacedPosition {

        public FacedPosition(@Nonnull  BlockPos pos, @Nonnull EnumFacing side){
            this.pos = pos;
            this.side = side;
        }

        private final BlockPos pos;
        private final EnumFacing side;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FacedPosition && pos.equals(((FacedPosition) obj).pos) && side == ((FacedPosition) obj).side;
        }

    }

}
