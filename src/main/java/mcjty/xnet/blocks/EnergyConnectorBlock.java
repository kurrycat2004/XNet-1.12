package mcjty.xnet.blocks;

import mcjty.lib.container.EmptyContainer;
import mcjty.xnet.XNet;
import mcjty.xnet.client.CableISBM;
import mcjty.xnet.varia.EnergyTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnergyConnectorBlock extends GenericCableBlock<EnergyConnectorTileEntity, EmptyContainer> {

    public EnergyConnectorBlock() {
        super(Material.iron, EnergyConnectorTileEntity.class, EmptyContainer.class, "energy_connector");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return CableISBM.modelResourceLocation;
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
    }

    @SideOnly(Side.CLIENT)
    public void initItemModel() {
        // For our item model we want to use a normal json model. This has to be called in
        // ClientProxy.init (not preInit) so that's why it is a separate method.
        Item itemBlock = GameRegistry.findItem(XNet.MODID, "energy_connector");
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        final int DEFAULT_ITEM_SUBTYPE = 0;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlock, DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // When our block is placed down we force a re-render of adjacent blocks to make sure their ISBM model is updated
        world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
    }

    @Override
    protected ConnectorType getConnectorType(IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericCableBlock) {
            return ConnectorType.CABLE;
        } else if (EnergyTools.isEnergyTE(world, pos)) {
            return ConnectorType.BLOCK;
        } else {
            return ConnectorType.NONE;
        }
    }

    @Override
    public String getConnectorTexture() {
        return XNet.MODID + ":blocks/energyConnector";
    }
}
