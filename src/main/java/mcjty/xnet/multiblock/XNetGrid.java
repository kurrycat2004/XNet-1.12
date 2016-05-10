package mcjty.xnet.multiblock;

import com.google.common.collect.Sets;
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

    public XNetGrid(){
        allLocations = Sets.newHashSet();
        allLocations_ = Collections.unmodifiableSet(allLocations);
        allConnectors = Sets.newHashSet();
    }

    private final Set<BlockPos> allLocations;
    private final Set<BlockPos> allLocations_;
    private final Set<FacedPosition> allConnectors;

    public void tick(){

    }

    public void invalidate(){
    }

    public Set<BlockPos> getAllLocations() {
        return allLocations_;
    }

    protected void merge(XNetGrid grid){
        this.allLocations.addAll(grid.allLocations);
    }

    protected void addTile(XNetTileData tile){
        allLocations.add(tile.getPos());
        tile.setGrid(this);
    }

    protected void onRemoved(XNetTileData tile){
    }

    void change(XNetTileData tile, IMultipart multipart){
        //TODO, called upon connactor added/removed
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
