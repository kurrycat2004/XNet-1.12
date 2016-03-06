package mcjty.xnet.multiblock;

import com.google.common.collect.Sets;
import net.minecraft.util.BlockPos;

import java.util.Set;

/**
 * Created by Elec332 on 6-3-2016.
 */
public class XNetGrid {

    public XNetGrid(){
        allLocations = Sets.newHashSet();
    }

    private Set<BlockPos> allLocations;

    public void tick(){

    }

    protected void merge(XNetGrid grid){
        this.allLocations.addAll(grid.allLocations);
    }

}
