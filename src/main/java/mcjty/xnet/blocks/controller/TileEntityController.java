package mcjty.xnet.blocks.controller;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.logic.*;
import mcjty.xnet.multiblock.ConsumerId;
import mcjty.xnet.multiblock.NetworkId;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.*;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public final class TileEntityController extends GenericEnergyReceiverTileEntity implements DefaultSidedInventory {

    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";
    public static final String CMD_GETCONNECTEDBLOCKS = "getConnectedBlocks";
    public static final String CLIENTCMD_CONNECTEDBLOCKSREADY = "connectedBlocksReady";

    public static final String CMD_CREATECHANNEL = "createChannel";
    public static final String CMD_CREATECONNECTOR = "createConnector";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ControllerContainer.factory, ControllerContainer.COUNT_FILTER_SLOTS);
    private NetworkId networkId;

    private final ChannelInfo[] channels = new ChannelInfo[MAX_CHANNELS];

    public TileEntityController() {
        super(100000, 1000); // @todo configurable
        for (int i = 0; i < MAX_CHANNELS; i++) {
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

        for (int i = 0; i < MAX_CHANNELS; i++) {
            if (channels[i] != null) {
                NBTTagCompound tc = new NBTTagCompound();
                tc.setString("type", channels[i].getType().getID());
                channels[i].writeToNBT(tc);
                tagCompound.setTag("channel" + i, tc);
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
        for (int i = 0; i < MAX_CHANNELS; i++) {
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
    private BlockPos findConsumerPosition(@Nonnull WorldBlob worldBlob, @Nonnull ConsumerId consumerId) {
        Set<BlockPos> consumers = worldBlob.getConsumers(networkId);
        for (BlockPos pos : consumers) {
            ConsumerId c = worldBlob.getConsumerAt(pos);
            if (consumerId.equals(c)) {
                return pos;
            }
        }
        throw new RuntimeException("This really cannot happen!");
    }

    @Nonnull
    private List<ConnectedBlockClientInfo> findConnectedBlocks() {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());

        Set<ConnectedBlockClientInfo> set = new HashSet<>();
        for (BlockPos consumerPos : worldBlob.getConsumers(networkId)) {
            ConsumerId consumerId = worldBlob.getConsumerAt(consumerPos);
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos pos = consumerPos.offset(facing);
                if (ConnectorBlock.isConnectable(getWorld(), pos)) {
                    SidedPos sidedPos = new SidedPos(pos, facing.getOpposite());
                    IBlockState state = getWorld().getBlockState(pos);
                    ItemStack item = state.getBlock().getItem(getWorld(), pos, state);
                    ConnectedBlockClientInfo info = new ConnectedBlockClientInfo(sidedPos, item);
                    System.out.println("findConnectedBlocks: sidedPos = " + sidedPos);
                    set.add(info);
                }
            }
        }
        List<ConnectedBlockClientInfo> list = new ArrayList<>(set);
        list.sort((i1, i2) -> {
            if (i1.getPos().getPos().equals(i2.getPos().getPos())) {
                return i1.getPos().getSide().compareTo(i2.getPos().getSide());
            } else {
                return i1.getPos().getPos().compareTo(i2.getPos().getPos());
            }
        });
        return list;
    }

    @Nonnull
    private List<ChannelClientInfo> findChannelInfo() {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());

        List<ChannelClientInfo> chanList = new ArrayList<>();
        for (ChannelInfo channel : channels) {
            if (channel != null) {
                ChannelClientInfo clientInfo = new ChannelClientInfo(channel.getType(),
                        channel.getChannelSettings());

                for (Map.Entry<SidedConsumer, ConnectorInfo> entry : channel.getConnectors().entrySet()) {
                    SidedConsumer sidedConsumer = entry.getKey();
                    ConnectorInfo info = entry.getValue();
                    if (info.getConnectorSettings() != null) {
                        BlockPos consumerPos = findConsumerPosition(worldBlob, sidedConsumer.getConsumerId());
                        SidedPos pos = new SidedPos(consumerPos.offset(sidedConsumer.getSide()), sidedConsumer.getSide().getOpposite());
                        ConnectorClientInfo ci = new ConnectorClientInfo(pos, sidedConsumer.getConsumerId(), channel.getType(), info.getConnectorSettings());
                        clientInfo.getConnectors().put(sidedConsumer, ci);
                        System.out.println("findChannelInfo: sidedPos = " + pos);
                    }
                }

                chanList.add(clientInfo);
            } else {
                chanList.add(null);
            }
        }
        return chanList;
    }

    private void createChannel(int index, String typeId) {
        IChannelType type = XNet.xNetApi.findType(typeId);
        channels[index] = new ChannelInfo(type);
        markDirty();
    }

    private void createConnector(int channel, SidedPos pos) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        ConsumerId consumerId = worldBlob.getConsumerAt(pos.getPos().offset(pos.getSide()));
        if (consumerId == null) {
            throw new RuntimeException("What?");
        }
        SidedConsumer id = new SidedConsumer(consumerId, pos.getSide().getOpposite());
        channels[channel].createConnector(id);
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
            int channel = args.get("channel").getInteger();
            SidedPos pos = new SidedPos(args.get("pos").getCoordinate(), EnumFacing.values()[args.get("side").getInteger()]);
            createConnector(channel, pos);
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
        if (CMD_GETCHANNELS.equals(command)) {
            return type.convert(findChannelInfo());
        } else if (CMD_GETCONNECTEDBLOCKS.equals(command)) {
            return type.convert(findConnectedBlocks());
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
            GuiController.fromServer_channels = new ArrayList<>(Type.create(ChannelClientInfo.class).convert(list));
            return true;
        } else if (CLIENTCMD_CONNECTEDBLOCKSREADY.equals(command)) {
            GuiController.fromServer_connectedBlocks = new ArrayList<>(Type.create(ConnectedBlockClientInfo.class).convert(list));
            return true;
        }
        return false;
    }
}
