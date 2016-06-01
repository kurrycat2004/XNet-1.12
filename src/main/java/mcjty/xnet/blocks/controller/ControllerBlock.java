package mcjty.xnet.blocks.controller;

import mcjty.lib.container.EmptyContainer;
import mcjty.xnet.blocks.GenericXNetBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ControllerBlock extends GenericXNetBlock<TileEntityController, EmptyContainer> {

    public ControllerBlock() {
        super(Material.IRON, TileEntityController.class, EmptyContainer.class, "controller", false);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        return false; //We don't have a GUI, and this prevents a possible NPE
    }

}
