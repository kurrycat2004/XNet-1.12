package mcjty.xnet.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mcjty.xnet.XNet;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.api.IXNetController;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.PartSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 6-3-2016.
 */
public class XNetGrid {

    public XNetGrid(XNetWorldGridRegistry registry){
        this.worldGridRegistry = registry;
        allLocations = Sets.newHashSet();
        allLocations_ = Collections.unmodifiableSet(allLocations);
        allConnectors = Lists.newArrayList();
    }

    private final Set<BlockPos> allLocations;
    private final Set<BlockPos> allLocations_;
    private final List<FacedPosition> allConnectors;
    private final XNetWorldGridRegistry worldGridRegistry;

    private XNetTileData controller;

    private int counter = 20;

    public void tick(){
        if (counter > 0) {
            counter--;
            return;
        }
        counter = 20;
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

    @Nullable
    public IXNetController getController() {
        return controller.getController();
    }

    @Nonnull
    public Set<BlockPos> getAllLocations() {
        return allLocations_;
    }

    @Nonnull
    public List<FacedPosition> getAllConnectors() {
        return allConnectors;
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
        allConnectors.addAll(grid.allConnectors);
        if (controller == null && grid.controller != null) {
            this.controller = grid.controller;
            grid.controller = null; //Just in  case
            this.controller.setGrid(this);
        } else if (controller != null && grid.controller != null){
            final XNetTileData otherController = grid.controller;
            grid.controller = null; //Just in  case
            otherController.getController().removeController();
        }
    }

    protected void addTile(XNetTileData tile){
        if (!tile.isController()) {
            allLocations.add(tile.getPos());
        } else {
            if (controller != null){ //Make it go poof
                tile.getController().removeController();
                return;
            } else {
                this.controller = tile;
            }
        }
        tile.setGrid(this);
    }

    protected void onRemoved(XNetTileData tile) {
        if (tile.isController()){
            controller = null;
        }
    }

    void change(XNetTileData tile, @Nullable IMultipart multipart_){
        TileEntity tileEntity = tile.getTile();
        BlockPos pos = tile.getPos();
        FacedPosition faces = null;
        for (FacedPosition position : allConnectors){
            if (position.pos.equals(pos)){
                faces = position;
            }
        }
        if (faces == null){
            faces = new FacedPosition(pos);
            allConnectors.add(faces);
        }
        faces.sides.clear();
        if (tileEntity instanceof IMultipartContainer){
            for (EnumFacing facing : EnumFacing.VALUES) {
                IMultipart multipart = ((IMultipartContainer) tileEntity).getPartInSlot(PartSlot.getFaceSlot(facing));
                if (multipart instanceof ICapabilityProvider && ((ICapabilityProvider) multipart).hasCapability(XNetAPI.XNET_CAPABILITY, facing.getOpposite())) {
                    faces.sides.add(facing);
                }
            }
        }
        if (faces.sides.isEmpty()){
            allConnectors.remove(faces);
        }
    }

    public class FacedPosition {

        private FacedPosition(@Nonnull BlockPos pos){
            this.pos = pos;
            this.sides = EnumSet.noneOf(EnumFacing.class);
        }

        private final BlockPos pos;
        private final Set<EnumFacing> sides;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FacedPosition && pos.equals(((FacedPosition) obj).pos) && sides.equals(((FacedPosition) obj).sides);
        }

        @Override
        public String toString() {
            return "Pos: "+pos+" Sides: "+sides;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Set<EnumFacing> getSides() {
            return sides;
        }
    }

}
