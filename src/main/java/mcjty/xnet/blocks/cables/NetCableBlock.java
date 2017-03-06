package mcjty.xnet.blocks.cables;

import mcjty.xnet.blocks.generic.CableColor;
import mcjty.xnet.blocks.generic.GenericCableBakedModel;
import mcjty.xnet.blocks.generic.GenericCableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class NetCableBlock extends GenericCableBlock {

    public static final String NETCABLE = "netcable";

    public NetCableBlock() {
        super(Material.CLOTH, NETCABLE);
    }

    public NetCableBlock(Material material, String name) {
        super(material, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        super.initModel();

        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return GenericCableBakedModel.modelCable;
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void initItemModel() {
//        // For our item model we want to use a normal json model. This has to be called in
//        // ClientProxy.init (not preInit) so that's why it is a separate method.
//        Item itemBlock = ForgeRegistries.ITEMS.getValue(new ResourceLocation(XNet.MODID, NETCABLE));
//        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
//        for (CableColor color : CableColor.VALUES) {
//            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlock, color.ordinal(), itemModelResourceLocation);
//        }
//    }

    @Override
    protected void clGetSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (CableColor value : CableColor.VALUES) {
            subItems.add(new ItemStack(itemIn, 1, value.ordinal()));
        }
    }

    @Override
    protected IBlockState clGetStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getPlacementState(world, pos, facing, hitX, hitY, hitZ, meta, placer);

    }

    public IBlockState getPlacementState(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        // When our block is placed down we force a re-render of adjacent blocks to make sure their ISBM model is updated
        world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
        return super.clGetStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @Override
    protected ConnectorType getConnectorType(@Nonnull CableColor color, IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if ((block instanceof NetCableBlock || block instanceof ConnectorBlock) && state.getValue(COLOR) == color) {
            return ConnectorType.CABLE;
        } else {
            return ConnectorType.NONE;
        }
    }

}
