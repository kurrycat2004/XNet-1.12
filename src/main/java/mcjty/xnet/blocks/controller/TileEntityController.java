package mcjty.xnet.blocks.controller;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.multiblock.NetworkId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.*;

public final class TileEntityController extends GenericTileEntity {

    public static final String CMD_GETCONSUMERS = "getConsumers";
    public static final String CLIENTCMD_CONSUMERSREADY = "consumersReady";


    private NetworkId networkId;

    public NetworkId getNetworkId() {
        return networkId;
    }

    public void setNetworkId(NetworkId networkId) {
        this.networkId = networkId;
        markDirty();
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
        if (networkId != null) {
            tagCompound.setInteger("networkId", networkId.getId());
        }
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        if (tagCompound.hasKey("networkId")) {
            networkId = new NetworkId(tagCompound.getInteger("networkId"));
        } else {
            networkId = null;
        }
    }

    @Nonnull
    private List<BlockPos> findConsumers() {
        List<BlockPos> consumers = new ArrayList<>();
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        for (BlockPos connectorPos : worldBlob.getConsumers(networkId)) {
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos p = connectorPos.offset(facing);
                if (ConnectorBlock.isConnectable(getWorld(), p)) {
                    consumers.add(p);
                }
            }
        }
        return consumers;
    }

    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, Map<String, Argument> args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (CMD_GETCONSUMERS.equals(command)) {
            return type.convert(findConsumers());
        }
        return Collections.emptyList();
    }

    @Override
    public <T> boolean execute(String command, List<T> list, Type<T> type) {
        boolean rc = super.execute(command, list, type);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_CONSUMERSREADY.equals(command)) {
            GuiController.fromServer_consumers = new ArrayList<>(Type.create(BlockPos.class).convert(list));
            return true;
        }
        return false;
    }
}
