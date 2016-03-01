package mcjty.xnet.blocks;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.varia.GenericXNetBlock;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.Container;

public class GenericCableBlock<T extends GenericTileEntity, C extends Container> extends GenericXNetBlock<T, C> {

    // Properties that indicate if there is the same block in a certain direction.
    public static final UnlistedPropertyBlockType NORTH = new UnlistedPropertyBlockType("north");
    public static final UnlistedPropertyBlockType SOUTH = new UnlistedPropertyBlockType("south");
    public static final UnlistedPropertyBlockType WEST = new UnlistedPropertyBlockType("west");
    public static final UnlistedPropertyBlockType EAST = new UnlistedPropertyBlockType("east");
    public static final UnlistedPropertyBlockType UP = new UnlistedPropertyBlockType("up");
    public static final UnlistedPropertyBlockType DOWN = new UnlistedPropertyBlockType("down");


    public GenericCableBlock(Material material, Class<? extends T> tileEntityClass, Class<? extends C> containerClass, String name) {
        super(material, tileEntityClass, containerClass, name, false);
    }

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    @Override
    public int getGuiID() {
        return -1;
    }
}
