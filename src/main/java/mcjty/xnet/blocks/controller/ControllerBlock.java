package mcjty.xnet.blocks.controller;

import mcjty.lib.container.EmptyContainer;
import mcjty.xnet.blocks.GenericXNetBlock;
import net.minecraft.block.material.Material;

public class ControllerBlock extends GenericXNetBlock<ControllerTE, EmptyContainer> {

    public ControllerBlock() {
        super(Material.IRON, ControllerTE.class, EmptyContainer.class, "controller", false);
    }

    @Override
    public int getGuiID() {
        return -1;
    }
}
