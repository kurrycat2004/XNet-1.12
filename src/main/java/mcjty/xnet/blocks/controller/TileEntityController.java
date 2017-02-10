package mcjty.xnet.blocks.controller;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.logic.ChannelInfo;
import mcjty.xnet.multiblock.NetworkId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import mcjty.xnet.network.PacketGetConsumers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public final class TileEntityController extends GenericEnergyReceiverTileEntity implements DefaultSidedInventory {

    public static final String CMD_GETCONSUMERS = "getConsumers";
    public static final String CLIENTCMD_CONSUMERSREADY = "consumersReady";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ControllerContainer.factory, ControllerContainer.COUNT_FILTER_SLOTS);
    private NetworkId networkId;

    private ChannelInfo[] channels = new ChannelInfo[MAX_CHANNELS];

    public TileEntityController() {
        super(100000, 1000); // @todo configurable
        for (int i = 0 ; i < MAX_CHANNELS ; i++) {
            channels[i] = null;
        }
    }

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
        writeBufferToNBT(tagCompound, inventoryHelper);
        if (networkId != null) {
            tagCompound.setInteger("networkId", networkId.getId());
        }
        for (int i = 0 ; i < MAX_CHANNELS ; i++) {
            if (channels[i] != null) {
                NBTTagCompound tc = new NBTTagCompound();
                tc.setString("type", channels[i].getType().getID());
                channels[i].writeToNBT(tc);
                tagCompound.setTag("channel"+i, tc);
            }
        }
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
        if (tagCompound.hasKey("networkId")) {
            networkId = new NetworkId(tagCompound.getInteger("networkId"));
        } else {
            networkId = null;
        }
        for (int i = 0 ; i < MAX_CHANNELS ; i++) {
            if (tagCompound.hasKey("channel" + i)) {
                NBTTagCompound tc = (NBTTagCompound) tagCompound.getTag("channel" + i);
                String id = tc.getString("type");
                IChannelType type = XNet.xNetApi.findType(id);
                if (type == null) {
                    XNet.logger.warn("Unsupported type " + id + "!");
                    continue;
                }
                channels[i] = new ChannelInfo(type);
                channels[i].readFromNBT(tc);
            } else {
                channels[i] = null;
            }
        }
    }

    @Nonnull
    private List<PacketGetConsumers.SidedPos> findConsumers() {
        List<PacketGetConsumers.SidedPos> consumers = new ArrayList<>();
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        for (BlockPos connectorPos : worldBlob.getConsumers(networkId)) {
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos p = connectorPos.offset(facing);
                if (ConnectorBlock.isConnectable(getWorld(), p)) {
                    consumers.add(new PacketGetConsumers.SidedPos(p, facing.getOpposite()));
                }
            }
        }
        return consumers;
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public boolean isUsable(EntityPlayer player) {
        return canPlayerAccess(player);
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
            GuiController.fromServer_consumers = new ArrayList<>(Type.create(PacketGetConsumers.SidedPos.class).convert(list));
            return true;
        }
        return false;
    }
}
