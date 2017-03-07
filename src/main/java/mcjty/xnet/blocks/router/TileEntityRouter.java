package mcjty.xnet.blocks.router;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.WorldTools;
import mcjty.typed.Type;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.api.keys.SidedPos;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.blocks.generic.CableColor;
import mcjty.xnet.blocks.generic.GenericCableBlock;
import mcjty.xnet.logic.ChannelInfo;
import mcjty.xnet.logic.ConnectedBlockClientInfo;
import mcjty.xnet.logic.ControllerChannelClientInfo;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public final class TileEntityRouter extends GenericTileEntity {

    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";

    public TileEntityRouter() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
    }

    @Nonnull
    private List<ControllerChannelClientInfo> findChannelInfo() {
        List<ControllerChannelClientInfo> list = new ArrayList<>();
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        for (EnumFacing facing : EnumFacing.VALUES) {
            IBlockState state = getWorld().getBlockState(getPos().offset(facing));
            Block block = state.getBlock();
            if (block instanceof ConnectorBlock && state.getValue(GenericCableBlock.COLOR) != CableColor.ADVANCED) {
                Set<NetworkId> networks = worldBlob.getNetworksAt(getPos().offset(facing));
                // @todo we only support one network!
                if (!networks.isEmpty()) {
                    NetworkId networkId = networks.iterator().next();
                    BlockPos controllerPos = worldBlob.findController(networkId);
                    if (controllerPos != null) {
                        TileEntity te = getWorld().getTileEntity(controllerPos);
                        if (te instanceof TileEntityController) {
                            TileEntityController controller = (TileEntityController) te;
                            for (int i = 0 ; i < MAX_CHANNELS ; i++) {
                                ChannelInfo channelInfo = controller.getChannels()[i];
                                if (channelInfo != null && !channelInfo.getChannelName().isEmpty()) {
                                    ControllerChannelClientInfo ci = new ControllerChannelClientInfo(channelInfo.getChannelName(), controllerPos, channelInfo.getType(), i);
                                    list.add(ci);
                                }
                            }
                        }
                    }
                }
            }
        }


        // @todo
        return list;
    }

    @Nullable
    private NetworkId findAdvancedNetwork() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            IBlockState state = getWorld().getBlockState(getPos().offset(facing));
            Block block = state.getBlock();
            if (block instanceof ConnectorBlock && state.getValue(GenericCableBlock.COLOR) == CableColor.ADVANCED) {
                WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
                Set<NetworkId> networks = worldBlob.getNetworksAt(getPos().offset(facing));
                // @todo we only support one network!
                if (!networks.isEmpty()) {
                    return networks.iterator().next();
                }
            }
        }
        return null;
    }

    public void addRoutedConnectors(Map<SidedConsumer, IConnectorSettings> connectors, String channelName) {
        NetworkId networkId = findAdvancedNetwork();
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        for (BlockPos consumerPos : worldBlob.getConsumers(networkId)) {
            if (WorldTools.chunkLoaded(getWorld(), consumerPos)) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    BlockPos pos = consumerPos.offset(facing);
                    TileEntity te = getWorld().getTileEntity(pos);
                    if (te instanceof TileEntityRouter) {
                        TileEntityRouter router = (TileEntityRouter) te;

                        // @todo

                    }
                }
            }
        }

    }


    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, Map<String, Argument> args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (CMD_GETCHANNELS.equals(command)) {
            return type.convert(findChannelInfo());
        }
        return Collections.emptyList();
    }

    @Override
    public <T> boolean execute(String command, List<T> list, Type<T> type) {
        boolean rc = super.execute(command, list, type);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_CHANNELSREADY.equals(command)) {
            GuiRouter.fromServer_channels = new ArrayList<>(Type.create(ControllerChannelClientInfo.class).convert(list));
            return true;
        }
        return false;
    }
}
