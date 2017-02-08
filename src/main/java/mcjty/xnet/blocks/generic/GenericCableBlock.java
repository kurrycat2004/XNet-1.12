package mcjty.xnet.blocks.generic;

import mcjty.lib.compat.CompatBlock;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.cables.ConnectorType;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class GenericCableBlock extends CompatBlock implements WailaInfoProvider, TOPInfoProvider {

    // Properties that indicate if there is the same block in a certain direction.
    public static final UnlistedPropertyBlockType NORTH = new UnlistedPropertyBlockType("north");
    public static final UnlistedPropertyBlockType SOUTH = new UnlistedPropertyBlockType("south");
    public static final UnlistedPropertyBlockType WEST = new UnlistedPropertyBlockType("west");
    public static final UnlistedPropertyBlockType EAST = new UnlistedPropertyBlockType("east");
    public static final UnlistedPropertyBlockType UP = new UnlistedPropertyBlockType("up");
    public static final UnlistedPropertyBlockType DOWN = new UnlistedPropertyBlockType("down");


    public GenericCableBlock(Material material, String name) {
        super(material);
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setUnlocalizedName(XNet.MODID + "." + name);
        setRegistryName(name);
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        setCreativeTab(XNet.tabXNet);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

    }

    public String getConnectorTexture() {
        return null;
    }

    @Override
    protected IBlockState clGetStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (!world.isRemote) {
//            CableNetwork cableNetwork = CableNetwork.getChannels(world);
//            GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) world.getTileEntity(pos);
//            int id;
//
//            Set<Integer> networks = findAdjacentNetworks(world, pos);
//            if (networks.isEmpty()) {
//                // Make a new network.
//                id = cableNetwork.newChannel();
//                genericCableTileEntity.setId(id);
//            } else if (networks.size() == 1) {
//                // Just connect to the existing network.
//                id = networks.iterator().next();
//                genericCableTileEntity.setId(id);
//            } else {
//                // Here we must merge the networks.
//                Iterator<Integer> iterator = networks.iterator();
//                int id1 = iterator.next();
//                int id2 = iterator.next();
//                cableNetwork.moveNetwork(world, id2, id1);
//                genericCableTileEntity.setId(id1);
//                id = id1;
//            }
//
//            CableNetwork.Network network = cableNetwork.getOrCreateNetwork(id);
//            network.add(pos);
//            cableNetwork.save(world);
        }
        return super.clGetStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
//            CableNetwork cableNetwork = CableNetwork.getChannels(world);
//            GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) world.getTileEntity(pos);
//            int id = genericCableTileEntity.getId();
//            CableNetwork.Network network = cableNetwork.getOrCreateNetwork(id);
//
//            Set<Integer> networks = findAdjacentNetworks(world, pos);
//            if (networks.isEmpty()) {
//                // There are no adjacent networks so we can just get rid of this one.
//                network.remove(pos);
//            } else {
//                // There are adjacent blocks. We possible have to split them.
//                // Set all blocks to -1 first.
//                for (BlockPos coordinate : network.getBlocks()) {
//                    TileEntity te = world.getTileEntity(coordinate);
//                    if (te instanceof GenericCableTileEntity) {
//                        GenericCableTileEntity gte = (GenericCableTileEntity) te;
//                        gte.setId(-1);
//                    }
//                }
//                // Now in every direction that there is a network we try to fully fill it.
//                for (EnumFacing facing : VALUES) {
//                    BlockPos p = pos.offset(facing);
//                    TileEntity te = world.getTileEntity(p);
//                    if (te instanceof GenericCableTileEntity) {
//                        GenericCableTileEntity gte = (GenericCableTileEntity) te;
//                        if (gte.getId() == -1) {
//                            Set<BlockPos> done = new HashSet<>();
//                            done.add(pos);
//                            connectNetwork(world, done, id, p);
//                            id = cableNetwork.newChannel();
//                        }
//                    }
//                }
//            }
//
//            cableNetwork.save(world);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
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
