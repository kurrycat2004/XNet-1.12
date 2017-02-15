package mcjty.xnet.blocks.cables;

import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.keys.ConsumerId;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.blocks.generic.GenericCableBlock;
import mcjty.xnet.blocks.generic.GenericCableISBM;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;

public class ConnectorBlock extends GenericCableBlock implements ITileEntityProvider {

    public static final String CONNECTOR = "connector";

    public ConnectorBlock() {
        super(Material.IRON, CONNECTOR);
        GameRegistry.registerTileEntity(ConnectorTileEntity.class, XNet.MODID + "_connector");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState metadata) {
        return new ConnectorTileEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
                return GenericCableISBM.modelConnector;
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
    }

    @SideOnly(Side.CLIENT)
    public void initItemModel() {
        // For our item model we want to use a normal json model. This has to be called in
        // ClientProxy.init (not preInit) so that's why it is a separate method.
        Item itemBlock = ForgeRegistries.ITEMS.getValue(new ResourceLocation(XNet.MODID, CONNECTOR));
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(getRegistryName(), "inventory");
        final int DEFAULT_ITEM_SUBTYPE = 0;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlock, DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);
    }

    @Override
    protected IBlockState clGetStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        // When our block is placed down we force a re-render of adjacent blocks to make sure their ISBM model is updated
        world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
        return super.clGetStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @Override
    protected ConnectorType getConnectorType(IBlockAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericCableBlock) {
            return ConnectorType.CABLE;
        } else if (isConnectable(world, pos)) {
            return ConnectorType.BLOCK;
        } else {
            return ConnectorType.NONE;
        }
    }

    public static boolean isConnectable(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            return false;
        }
        if (te instanceof IInventory) {
            return true;
        }
        if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            return true;
        }
        if (te instanceof TileEntityController) {
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess blockAccess, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = super.getDrops(blockAccess, pos, state, fortune);
        if (blockAccess instanceof World) {
            World world = (World) blockAccess;
            for (ItemStack drop : drops) {
                if (!drop.hasTagCompound()) {
                    drop.setTagCompound(new NBTTagCompound());
                }
                WorldBlob worldBlob = XNetBlobData.getBlobData(world).getWorldBlob(world);
                ConsumerId consumer = worldBlob.getConsumerAt(pos);
                if (consumer != null) {
                    drop.getTagCompound().setInteger("consumerId", consumer.getId());
                }
            }
        }

        return drops;
    }

    @Override
    protected void createCableSegment(World world, BlockPos pos, ItemStack stack) {
        XNetBlobData blobData = XNetBlobData.getBlobData(world);
        WorldBlob worldBlob = blobData.getWorldBlob(world);
        ConsumerId consumer;
        if (ItemStackTools.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey("consumerId")) {
            consumer = new ConsumerId(stack.getTagCompound().getInteger("consumerId"));
        } else {
            consumer = worldBlob.newConsumer();
        }
        worldBlob.createNetworkConsumer(pos, STANDARD_COLOR, consumer);
        blobData.save(world);
    }

    @Override
    public String getConnectorTexture() {
        return XNet.MODID + ":blocks/connector";
    }
}
