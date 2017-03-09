package mcjty.xnet.blocks.router;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.blocks.generic.CableColor;
import mcjty.xnet.blocks.generic.GenericXNetBlock;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.multiblock.BlobId;
import mcjty.xnet.multiblock.ColorId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public class RouterBlock extends GenericXNetBlock<TileEntityRouter, EmptyContainer> {

    public RouterBlock() {
        super(Material.IRON, TileEntityRouter.class, EmptyContainer.class, "router", false);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_ROUTER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiRouter.class;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        XNetBlobData blobData = XNetBlobData.getBlobData(world);
        WorldBlob worldBlob = blobData.getWorldBlob(world);
        Set<NetworkId> networks = worldBlob.getNetworksAt(data.getPos());
        for (NetworkId networkId : networks) {
            probeInfo.text(TextStyleClass.LABEL + "Network: " + TextStyleClass.INFO + networkId.getId());
        }

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
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            XNetBlobData blobData = XNetBlobData.getBlobData(world);
            WorldBlob worldBlob = blobData.getWorldBlob(world);
            NetworkId networkId = worldBlob.newNetwork();
            worldBlob.createNetworkProvider(pos, new ColorId(CableColor.ADVANCED.ordinal()+1), networkId);
            blobData.save(world);
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


}
