package mcjty.xnet.blocks;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.varia.GenericXNetBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GenericCableBlock<T extends GenericTileEntity, C extends Container> extends GenericXNetBlock<T, C> {

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

    public String getConnectorTexture() {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    protected BlockState createBlockState() {
        IProperty[] listedProperties = new IProperty[0]; // no listed properties
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { NORTH, SOUTH, WEST, EAST, UP, DOWN };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;

        ConnectorType north = getConnectorType(world, pos.north());
        ConnectorType south = getConnectorType(world, pos.south());
        ConnectorType west = getConnectorType(world, pos.west());
        ConnectorType east = getConnectorType(world, pos.east());
        ConnectorType up = getConnectorType(world, pos.up());
        ConnectorType down = getConnectorType(world, pos.down());

        return extendedBlockState
                .withProperty(NORTH, north)
                .withProperty(SOUTH, south)
                .withProperty(WEST, west)
                .withProperty(EAST, east)
                .withProperty(UP, up)
                .withProperty(DOWN, down);
    }

    protected abstract ConnectorType getConnectorType(IBlockAccess world, BlockPos pos);
}
