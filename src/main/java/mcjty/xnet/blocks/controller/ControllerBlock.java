package mcjty.xnet.blocks.controller;

import elec332.core.tile.BlockTileBase;
import elec332.core.world.WorldHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.varia.XNetResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 30-5-2016.
 */
@SuppressWarnings("all")
public class ControllerBlock extends BlockTileBase {

    public static final PropertyBool ACTIVE_PROPERTY = PropertyBool.create("active");

    public ControllerBlock(){
        super(Material.ROCK, null, new XNetResourceLocation("controller"));
        this.setHardness(2.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 0);
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE_PROPERTY, true));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!state.getValue(ACTIVE_PROPERTY)){
            TileEntity tile = WorldHelper.getTileAt(worldIn, pos);
            if (tile instanceof TileEntityInactiveController){
                if (!worldIn.isRemote) {
                    ((TileEntityInactiveController) tile).activate();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState metadata, int fortune) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof GenericTileEntity) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((GenericTileEntity)tileEntity).writeRestorableToNBT(tagCompound);
            stack.setTagCompound(tagCompound);
            ArrayList result = new ArrayList();
            result.add(stack);
            return result;
        } else {
            return super.getDrops(world, pos, metadata, fortune);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(ACTIVE_PROPERTY)){
            return new TileEntityController();
        } else {
            return new TileEntityInactiveController();
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{ACTIVE_PROPERTY});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta == 0 ? getDefaultState() : getDefaultState().withProperty(ACTIVE_PROPERTY, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE_PROPERTY) ? 0 : 1;
    }

}
