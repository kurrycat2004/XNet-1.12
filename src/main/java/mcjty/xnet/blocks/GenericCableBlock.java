package mcjty.xnet.blocks;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.multiblock.CableNetwork;
import mcjty.xnet.varia.GenericXNetBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static net.minecraft.util.EnumFacing.VALUES;

public abstract class GenericCableBlock<T extends GenericTileEntity, C extends Container> extends GenericXNetBlock<T, C> {




    public GenericCableBlock(Material material, Class<? extends T> tileEntityClass, Class<? extends C> containerClass, String name) {
        super(material, tileEntityClass, containerClass, name, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof GenericCableTileEntity) {
            GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) tileEntity;
            currenttip.add(EnumChatFormatting.GREEN + "ID: " + genericCableTileEntity.getId());
        }
        return currenttip;
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
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            CableNetwork cableNetwork = CableNetwork.getChannels(world);
            GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) world.getTileEntity(pos);
            int id;

            Set<Integer> networks = findAdjacentNetworks(world, pos);
            if (networks.isEmpty()) {
                // Make a new network.
                id = cableNetwork.newChannel();
                genericCableTileEntity.setId(id);
            } else if (networks.size() == 1) {
                // Just connect to the existing network.
                id = networks.iterator().next();
                genericCableTileEntity.setId(id);
            } else {
                // Here we must merge the networks.
                Iterator<Integer> iterator = networks.iterator();
                int id1 = iterator.next();
                int id2 = iterator.next();
                cableNetwork.moveNetwork(world, id2, id1);
                genericCableTileEntity.setId(id1);
                id = id1;
            }

            CableNetwork.Network network = cableNetwork.getOrCreateNetwork(id);
            network.add(pos);
            cableNetwork.save(world);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            CableNetwork cableNetwork = CableNetwork.getChannels(world);
            GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) world.getTileEntity(pos);
            int id = genericCableTileEntity.getId();
            CableNetwork.Network network = cableNetwork.getOrCreateNetwork(id);

            Set<Integer> networks = findAdjacentNetworks(world, pos);
            if (networks.isEmpty()) {
                // There are no adjacent networks so we can just get rid of this one.
                network.remove(pos);
            } else {
                // There are adjacent blocks. We possible have to split them.
                // Set all blocks to -1 first.
                for (BlockPos coordinate : network.getBlocks()) {
                    TileEntity te = world.getTileEntity(coordinate);
                    if (te instanceof GenericCableTileEntity) {
                        GenericCableTileEntity gte = (GenericCableTileEntity) te;
                        gte.setId(-1);
                    }
                }
                // Now in every direction that there is a network we try to fully fill it.
                for (EnumFacing facing : VALUES) {
                    BlockPos p = pos.offset(facing);
                    TileEntity te = world.getTileEntity(p);
                    if (te instanceof GenericCableTileEntity) {
                        GenericCableTileEntity gte = (GenericCableTileEntity) te;
                        if (gte.getId() == -1) {
                            Set<BlockPos> done = new HashSet<>();
                            done.add(pos);
                            connectNetwork(world, done, id, p);
                            id = cableNetwork.newChannel();
                        }
                    }
                }
            }

            cableNetwork.save(world);
        }

        super.breakBlock(world, pos, state);
    }

    private void connectNetwork(World world, Set<BlockPos> done, int id, BlockPos pos) {
        if (done.contains(pos)) {
            return;
        }
        done.add(pos);
        TileEntity te = world.getTileEntity(pos);
        GenericCableTileEntity gte = (GenericCableTileEntity) te;
        if (gte.getId() != -1) {
            return;
        }
        gte.setId(id);

        for (EnumFacing facing : VALUES) {
            BlockPos p = pos.offset(facing);
            te = world.getTileEntity(p);
            if (te instanceof GenericCableTileEntity) {
                connectNetwork(world, done, id, p);
            }
        }
    }

    private Set<Integer> findAdjacentNetworks(World world, BlockPos pos) {
        Set<Integer> networks = new HashSet<>();
        for (EnumFacing facing : VALUES) {
            BlockPos p = pos.offset(facing);
            TileEntity te = world.getTileEntity(p);
            if (te instanceof GenericCableTileEntity) {
                GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) te;
                networks.add(genericCableTileEntity.getId());
            }
        }
        return networks;
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

    protected abstract ConnectorType getConnectorType(IBlockAccess world, BlockPos pos);
}
