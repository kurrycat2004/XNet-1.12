package mcjty.xnet.blocks.controller;

import mcjty.lib.container.EmptyContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.xnet.blocks.generic.GenericXNetBlock;
import mcjty.xnet.multiblock.NetworkId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import static mcjty.xnet.blocks.generic.GenericCableBlock.STANDARD_COLOR;

public class ControllerBlock extends GenericXNetBlock<TileEntityController, EmptyContainer> {

    public ControllerBlock() {
        super(Material.IRON, TileEntityController.class, EmptyContainer.class, "controller", false);
    }

    @Override
    public int getGuiID() {
        return -1;
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer player) {
        return false; //We don't have a GUI, and this prevents a possible NPE
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            XNetBlobData blobData = XNetBlobData.getBlobData(world);
            WorldBlob worldBlob = blobData.getWorldBlob(world);
            NetworkId networkId = worldBlob.newNetwork();
            worldBlob.createNetworkProvider(pos, STANDARD_COLOR, networkId);
            blobData.save(world);

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityController) {
                ((TileEntityController) te).setNetworkId(networkId);
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            XNetBlobData blobData = XNetBlobData.getBlobData(world);
            WorldBlob worldBlob = blobData.getWorldBlob(world);
            worldBlob.removeCableSegment(pos);
            blobData.save(world);
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        TileEntity te = world.getTileEntity(data.getPos());
        if (te instanceof TileEntityController) {
            TileEntityController controller = (TileEntityController) te;
            NetworkId networkId = controller.getNetworkId();
            if (networkId != null) {
                probeInfo.text(TextStyleClass.LABEL + "Network: " + TextStyleClass.INFO + networkId.getId());
            }
        }
    }
}
