package mcjty.xnet.blocks.controller;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.blocks.controller.gui.GuiController;
import mcjty.xnet.blocks.generic.GenericXNetBlock;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.multiblock.BlobId;
import mcjty.xnet.multiblock.ColorId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ControllerBlock extends GenericXNetBlock<TileEntityController, ControllerContainer> {

    public static final PropertyBool ERROR = PropertyBool.create("error");

    public ControllerBlock() {
        super(Material.IRON, TileEntityController.class, ControllerContainer.class, "controller", false);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_CONTROLLER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiController.class;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            findNeighbourConnector(world, pos);
//            XNetBlobData blobData = XNetBlobData.getBlobData(world);
//            WorldBlob worldBlob = blobData.getWorldBlob(world);
//            NetworkId networkId = worldBlob.newNetwork();
//            worldBlob.createNetworkProvider(pos, STANDARD_COLOR, networkId);
//            blobData.save(world);

//            TileEntity te = world.getTileEntity(pos);
//            if (te instanceof TileEntityController) {
//                ((TileEntityController) te).setNetworkId(networkId);
//            }
        }
    }

    @Override
    protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
        super.clOnNeighborChanged(state, world, pos, blockIn);
        if (!world.isRemote) {
            findNeighbourConnector(world, pos);
        }
    }


    // Check neighbour blocks for a connector and inherit the color from that
    private void findNeighbourConnector(World world, BlockPos pos) {
        XNetBlobData blobData = XNetBlobData.getBlobData(world);
        WorldBlob worldBlob = blobData.getWorldBlob(world);
        ColorId oldColor = worldBlob.getColorAt(pos);
        ColorId newColor = null;
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(facing)).getBlock() instanceof ConnectorBlock) {
                ColorId color = worldBlob.getColorAt(pos.offset(facing));
                if (color != null) {
                    if (color == oldColor) {
                        return; // Nothing to do
                    }
                    newColor = color;
                }
            }
        }
        if (newColor != null) {
            if (worldBlob.getBlobAt(pos) != null) {
                worldBlob.removeCableSegment(pos);
            }
            NetworkId networkId = worldBlob.newNetwork();
            worldBlob.createNetworkProvider(pos, newColor, networkId);
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

            if (mode == ProbeMode.DEBUG) {
                String s = "";
                for (NetworkId id : controller.getNetworkChecker().getAffectedNetworks()) {
                    s += id.getId() + " ";
                    if (s.length() > 15) {
                        probeInfo.text(TextStyleClass.LABEL + "InfNet: " + TextStyleClass.INFO + s);
                        s = "";
                    }
                }
                if (!s.isEmpty()) {
                    probeInfo.text(TextStyleClass.LABEL + "InfNet: " + TextStyleClass.INFO + s);
                }
            }
            if (controller.inError()) {
                probeInfo.text(TextStyleClass.ERROR + "Too many controllers on network!");
            }
        }

        WorldBlob worldBlob = XNetBlobData.getBlobData(world).getWorldBlob(world);
        if (mode == ProbeMode.DEBUG) {
            BlobId blobId = worldBlob.getBlobAt(data.getPos());
            if (blobId != null) {
                probeInfo.text(TextStyleClass.LABEL + "Blob: " + TextStyleClass.INFO + blobId.getId());
            }
            ColorId colorId = worldBlob.getColorAt(data.getPos());
            if (colorId != null) {
                probeInfo.text(TextStyleClass.LABEL + "Color: " + TextStyleClass.INFO + colorId.getId());
            }


        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        boolean error = false;
        if (te instanceof TileEntityController) {
            error = ((TileEntityController)te).inError();
        }
        return state.withProperty(ERROR, error);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ERROR);
    }

}
