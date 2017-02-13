package mcjty.xnet.blocks.controller;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.logic.ChannelInfo;
import mcjty.xnet.logic.ConnectorClientInfo;
import mcjty.xnet.logic.SidedConsumer;
import mcjty.xnet.logic.SidedPos;
import mcjty.xnet.multiblock.ConsumerId;
import mcjty.xnet.multiblock.NetworkId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";

    public static final String CMD_CREATECHANNEL = "createChannel";
    public static final String CMD_CREATECONNECTOR = "createConnector";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ControllerContainer.factory, ControllerContainer.COUNT_FILTER_SLOTS);
    private NetworkId networkId;

    private final ChannelInfo[] channels = new ChannelInfo[MAX_CHANNELS];

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
    private List<ConnectorClientInfo> findConsumers() {
        List<ConnectorClientInfo> consumers = new ArrayList<>();
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        for (BlockPos connectorPos : worldBlob.getConsumers(networkId)) {
            ConsumerId consumerId = worldBlob.getConsumerAt(connectorPos);
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos p = connectorPos.offset(facing);
                if (ConnectorBlock.isConnectable(getWorld(), p)) {
                    if (consumerId != null) {
                        consumers.add(new ConnectorClientInfo(new SidedPos(p, facing.getOpposite()), consumerId));
                    }
                }
            }
        }
        return consumers;
    }

    @Nonnull
    private List<ChannelInfo> findChannelInfo() {
        List<ChannelInfo> chanList = new ArrayList<>();
        Collections.addAll(chanList, channels);
        return chanList;
    }

    private void createChannel(int index, String typeId) {
        IChannelType type = XNet.xNetApi.findType(typeId);
        channels[index] = new ChannelInfo(type);
        markDirty();
    }

    private void createConnector(int index, SidedConsumer id) {
        channels[index].createConnector(id);
        markDirty();
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public boolean isUsable(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_CREATECHANNEL.equals(command)) {
            int index = args.get("index").getInteger();
            String typeId = args.get("type").getString();
            createChannel(index, typeId);
            return true;
        } else if (CMD_CREATECONNECTOR.equals(command)) {
            int index = args.get("index").getInteger();
            SidedConsumer sidedConsumer = new SidedConsumer(new ConsumerId(args.get("consumer").getInteger()), EnumFacing.values()[args.get("side").getInteger()]);
            createConnector(index, sidedConsumer);
            return true;
        }
        return false;
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
        } else if (CMD_GETCHANNELS.equals(command)) {
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
        if (CLIENTCMD_CONSUMERSREADY.equals(command)) {
            GuiController.fromServer_connectors = new ArrayList<>(Type.create(ConnectorClientInfo.class).convert(list));
            return true;
        } else if (CLIENTCMD_CHANNELSREADY.equals(command)) {
            GuiController.fromServer_channels = new ArrayList<>(Type.create(ChannelInfo.class).convert(list));
            return true;
        }
        return false;
    }
}
