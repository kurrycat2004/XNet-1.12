package mcjty.xnet.blocks.redstoneproxy;

import mcjty.lib.McJtyRegister;
import mcjty.xnet.XNet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class RedstoneProxyBlock extends Block {

    public RedstoneProxyBlock() {
        super(Material.IRON);
        setUnlocalizedName(XNet.MODID + ".redstone_proxy");
        setRegistryName("redstone_proxy");
        McJtyRegister.registerLater(this, XNet.instance, null, null);
        McJtyRegister.registerLater(new ItemBlock(this).setRegistryName(getRegistryName()), XNet.instance);
        setCreativeTab(XNet.tabXNet);
    }

    public static boolean activateBlock(Block block, World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return block.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    public static Collection<IProperty<?>> getPropertyKeys(IBlockState state) {
        return state.getPropertyKeys();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


    public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, null, tooltip, null);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        clAddInformation(stack, null, tooltip, false);  // @todo WRONG
    }

    protected void clAddCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> list, Entity entity) {
        super.addCollisionBoxToList(state, world, pos, entityBox, list, entity, false);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        clAddCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
    }

    protected void clOnNeighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {

    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_) {
        clOnNeighborChanged(state, worldIn, pos, blockIn);
    }

    protected boolean clOnBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return clOnBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return clGetStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    protected IBlockState clGetStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
        clGetSubBlocks(Item.getItemFromBlock(this), itemIn, tab);
    }

    @SideOnly(Side.CLIENT)
    protected void clGetSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        super.getSubBlocks(tab, (NonNullList<ItemStack>) subItems);
    }
}
