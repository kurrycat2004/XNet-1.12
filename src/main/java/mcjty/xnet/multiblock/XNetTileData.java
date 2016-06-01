package mcjty.xnet.multiblock;

import elec332.core.grid.capability.ITileData;
import mcjty.xnet.api.IXNetController;
import mcjty.xnet.blocks.controller.TileEntityController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 6-3-2016.
 */
public class XNetTileData implements ITileData {

    protected XNetTileData(TileEntity tile){
        this.tile = tile;
        this.controller = tile.getClass() == TileEntityController.class;
    }

    private final TileEntity tile;
    private final boolean controller;
    private XNetGrid grid;

    @Override
    public BlockPos getPos() {
        return tile.getPos();
    }

    @Override
    public TileEntity getTile() {
        return tile;
    }

    @Nullable
    public XNetGrid getCurrentGrid(){
        return grid;
    }

    public XNetTileData setGrid(XNetGrid grid){
        this.grid = grid;
        return this;
    }

    public boolean isController(){
        return controller;
    }

    public IXNetController getController(){
        if (!controller){
            throw new IllegalStateException();
        }
        return ((TileEntityController)tile).getController();
    }

}
