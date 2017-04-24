package mcjty.xnet.blocks.controller;

import mcjty.lib.entity.GenericEnergyReceiverTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.BlockPosTools;
import mcjty.typed.Type;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.keys.ConsumerId;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.api.keys.SidedPos;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.blocks.cables.ConnectorTileEntity;
import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.gui.GuiController;
import mcjty.xnet.clientinfo.ChannelClientInfo;
import mcjty.xnet.clientinfo.ConnectedBlockClientInfo;
import mcjty.xnet.clientinfo.ConnectorClientInfo;
import mcjty.xnet.clientinfo.ConnectorInfo;
import mcjty.xnet.config.GeneralConfiguration;
import mcjty.xnet.logic.ChannelInfo;
import mcjty.xnet.logic.LogicTools;
import mcjty.xnet.multiblock.NetworkChecker;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public final class TileEntityController extends GenericEnergyReceiverTileEntity implements ITickable, IControllerContext {

    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";
    public static final String CMD_GETCONNECTEDBLOCKS = "getConnectedBlocks";
    public static final String CLIENTCMD_CONNECTEDBLOCKSREADY = "connectedBlocksReady";

    public static final String CMD_CREATECONNECTOR = "createConnector";
    public static final String CMD_REMOVECONNECTOR = "removeConnector";
    public static final String CMD_UPDATECONNECTOR = "updateConnector";

    public static final String CMD_CREATECHANNEL = "createChannel";
    public static final String CMD_REMOVECHANNEL = "removeChannel";
    public static final String CMD_UPDATECHANNEL = "updateChannel";

    private NetworkId networkId;

    private final ChannelInfo[] channels = new ChannelInfo[MAX_CHANNELS];
    private int colors = 0;

    // Client side only
    private boolean error = false;

    // Cached/transient data
    private Map<SidedConsumer, IConnectorSettings> cachedConnectors[] = new Map[MAX_CHANNELS];
    private Map<SidedConsumer, IConnectorSettings> cachedRoutedConnectors[] = new Map[MAX_CHANNELS];

    private NetworkChecker networkChecker = null;

    public TileEntityController() {
        super(100000, 1000); // @todo configurable
        for (int i = 0; i < MAX_CHANNELS; i++) {
            channels[i] = null;
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        boolean oldError = error;

        super.onDataPacket(net, packet);

        if (getWorld().isRemote) {
            // If needed send a render update.
            if (oldError != error) {
                getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }
    }


    @Nonnull
    public NetworkChecker getNetworkChecker() {
        if (networkChecker == null) {
            networkChecker = new NetworkChecker();
            networkChecker.add(networkId);
            WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
            LogicTools.routers(getWorld(), networkId)
                    .forEach(router -> {
                            networkChecker.add(worldBlob.getNetworksAt(router.getPos()));
                            // We're only interested in one network. The other router networks are all same topology
                            NetworkId routerNetwork = worldBlob.getNetworkAt(router.getPos());
                            if (routerNetwork != null) {
                                LogicTools.routers(getWorld(), routerNetwork)
                                        .filter(r -> router != r)
                                        .forEach(r -> LogicTools.connectors(getWorld(), r.getPos())
                                                .forEach(connectorPos -> networkChecker.add(worldBlob.getNetworkAt(connectorPos))));
                            }
                    });

//            networkChecker.dump();
        }
        return networkChecker;
    }

    @Override
    public World getControllerWorld() {
        return getWorld();
    }

    @Override
    public NetworkId getNetworkId() {
        return networkId;
    }

    public void setNetworkId(NetworkId networkId) {
        this.networkId = networkId;
        markDirtyQuick();
    }

    public ChannelInfo[] getChannels() {
        return channels;
    }

    private void checkNetwork(WorldBlob worldBlob) {
        if (networkId != null && getNetworkChecker().isDirtyAndMarkClean(worldBlob)) {
            for (int i = 0 ; i < MAX_CHANNELS ; i++) {
                if (channels[i] != null) {
                    cleanCache(i);
                }
            }
        }
    }

    @Override
    public boolean matchColor(int colorMask) {
        return (colors & colorMask) == colorMask;
    }

    public boolean inError() {
        if (getWorld().isRemote) {
            return error;
        } else {
            WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
            return worldBlob.getNetworksAt(getPos()).size() > 1;
        }
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());

            if (worldBlob.getNetworksAt(getPos()).size() > 1) {
                // Error situation!
                markDirtyClient();
                return;
            }

            checkNetwork(worldBlob);

            if (!checkAndConsumeRF(GeneralConfiguration.controllerRFT)) {
                return;
            }

            boolean dirty = false;
            int newcolors = 0;
            for (int i = 0; i < MAX_CHANNELS; i++) {
                if (channels[i] != null && channels[i].isEnabled()) {
                    if (checkAndConsumeRF(GeneralConfiguration.controllerChannelRFT)) {
                        channels[i].getChannelSettings().tick(i, this);
                    }
                    newcolors |= channels[i].getChannelSettings().getColors();
                    dirty = true;
                }
            }
            if (newcolors != colors) {
                dirty = true;
                colors = newcolors;
            }
            if (dirty) {
                markDirtyQuick();
            }
        }
    }

    @Override
    public boolean checkAndConsumeRF(int rft) {
        if (rft > 0) {
            if (getEnergyStored() < rft) {
                // Not enough energy
                return false;
            }
            consumeEnergy(rft);
            markDirtyQuick();
        }
        return true;
    }

    private void networkDirty() {
        if (networkId != null) {
            XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld()).incNetworkVersion(networkId);
        }
    }

    private void cleanCache(int channel) {
        cachedConnectors[channel] = null;
        cachedRoutedConnectors[channel] = null;
        channels[channel].getChannelSettings().cleanCache();
    }

    @Override
    @Nonnull
    public Map<SidedConsumer, IConnectorSettings> getConnectors(int channel) {
        if (cachedConnectors[channel] == null) {
            cachedConnectors[channel] = new HashMap<>();
            for (Map.Entry<SidedConsumer, ConnectorInfo> entry : channels[channel].getConnectors().entrySet()) {
                cachedConnectors[channel].put(entry.getKey(), entry.getValue().getConnectorSettings());
            }
        }
        return cachedConnectors[channel];
    }

    @Override
    @Nonnull
    public Map<SidedConsumer, IConnectorSettings> getRoutedConnectors(int channel) {
        if (cachedRoutedConnectors[channel] == null) {

            cachedRoutedConnectors[channel] = new HashMap<>();

            if (!channels[channel].getChannelName().isEmpty()) {
                LogicTools.routers(getWorld(), networkId)
                        .forEach(router -> router.addRoutedConnectors(cachedRoutedConnectors[channel], getPos(), channel, channels[channel].getType()));
            }
        }
        return cachedRoutedConnectors[channel];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        if (networkId != null) {
            tagCompound.setInteger("networkId", networkId.getId());
        }
        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        if (tagCompound.hasKey("networkId")) {
            networkId = new NetworkId(tagCompound.getInteger("networkId"));
        } else {
            networkId = null;
        }
    }

    @Override
    public void writeClientDataToNBT(NBTTagCompound tagCompound) {
        super.writeClientDataToNBT(tagCompound);
        if (!getWorld().isRemote) {
            tagCompound.setBoolean("error", inError());
        }
    }

    @Override
    public void readClientDataFromNBT(NBTTagCompound tagCompound) {
        super.readClientDataFromNBT(tagCompound);
        error = tagCompound.getBoolean("error");
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        tagCompound.setInteger("colors", colors);

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
        colors = tagCompound.getInteger("colors");
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

    @Nullable
    @Override
    public BlockPos findConsumerPosition(@Nonnull ConsumerId consumerId) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        return findConsumerPosition(worldBlob, consumerId);
    }

    @Nullable
    private BlockPos findConsumerPosition(@Nonnull WorldBlob worldBlob, @Nonnull ConsumerId consumerId) {
        return worldBlob.getConsumerPosition(consumerId);
    }

    @Nonnull
    private List<ConnectedBlockClientInfo> findConnectedBlocks() {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());

        Set<ConnectedBlockClientInfo> set = new HashSet<>();
        for (BlockPos consumerPos : worldBlob.getConsumers(networkId)) {
            String name = "";
            TileEntity te = getWorld().getTileEntity(consumerPos);
            if (te instanceof ConnectorTileEntity) {
                // Should always be the case. @todo error?
                name = ((ConnectorTileEntity) te).getConnectorName();
            } else {
                XNet.logger.warn("What? The connector at " + BlockPosTools.toString(consumerPos) + " is not a connector?");
            }
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos pos = consumerPos.offset(facing);
                if (ConnectorBlock.isConnectable(getWorld(), pos)) {
                    SidedPos sidedPos = new SidedPos(pos, facing.getOpposite());
                    IBlockState state = getWorld().getBlockState(pos);
                    ItemStack item = state.getBlock().getItem(getWorld(), pos, state);
                    ConnectedBlockClientInfo info = new ConnectedBlockClientInfo(sidedPos, item, name);
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
                ChannelClientInfo clientInfo = new ChannelClientInfo(channel.getChannelName(), channel.getType(),
                        channel.getChannelSettings(), channel.isEnabled());

                for (Map.Entry<SidedConsumer, ConnectorInfo> entry : channel.getConnectors().entrySet()) {
                    SidedConsumer sidedConsumer = entry.getKey();
                    ConnectorInfo info = entry.getValue();
                    if (info.getConnectorSettings() != null) {
                        BlockPos consumerPos = findConsumerPosition(worldBlob, sidedConsumer.getConsumerId());
                        if (consumerPos != null) {
                            SidedPos pos = new SidedPos(consumerPos.offset(sidedConsumer.getSide()), sidedConsumer.getSide().getOpposite());
                            boolean advanced = getWorld().getBlockState(consumerPos).getBlock() == NetCableSetup.advancedConnectorBlock;
                            ConnectorClientInfo ci = new ConnectorClientInfo(pos, sidedConsumer.getConsumerId(), channel.getType(), info.getConnectorSettings(), advanced);
                            clientInfo.getConnectors().put(sidedConsumer, ci);
                        } else {
                            // Consumer was possibly removed. We might want to remove the entry from our list here?
                            // @todo
                        }
                    }
                }

                chanList.add(clientInfo);
            } else {
                chanList.add(null);
            }
        }
        return chanList;
    }

    private void updateChannel(int channel, Map<String, Argument> args) {
        Map<String, Object> data = new HashMap<>();
        for (Map.Entry<String, Argument> e : args.entrySet()) {
            data.put(e.getKey(), e.getValue().getValue());
        }
        channels[channel].getChannelSettings().update(data);

        Boolean enabled = (Boolean) data.get(GuiController.TAG_ENABLED);
        channels[channel].setEnabled(Boolean.TRUE.equals(enabled));

        String name = (String) data.get(GuiController.TAG_NAME);
        channels[channel].setChannelName(name);

        networkDirty();
        markDirtyQuick();
    }

    private void removeChannel(int channel) {
        channels[channel] = null;
        cachedConnectors[channel] = null;
        cachedRoutedConnectors[channel] = null;
        networkDirty();
        markDirtyQuick();
    }

    private void createChannel(int channel, String typeId) {
        IChannelType type = XNet.xNetApi.findType(typeId);
        channels[channel] = new ChannelInfo(type);
        networkDirty();
        markDirtyQuick();
    }

    private void updateConnector(int channel, SidedPos pos, Map<String, Argument> args) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        ConsumerId consumerId = worldBlob.getConsumerAt(pos.getPos().offset(pos.getSide()));
        for (Map.Entry<SidedConsumer, ConnectorInfo> entry : channels[channel].getConnectors().entrySet()) {
            SidedConsumer key = entry.getKey();
            if (key.getConsumerId().equals(consumerId) && key.getSide().getOpposite().equals(pos.getSide())) {
                Map<String, Object> data = new HashMap<>();
                for (Map.Entry<String, Argument> e : args.entrySet()) {
                    data.put(e.getKey(), e.getValue().getValue());
                }
                channels[channel].getConnectors().get(key).getConnectorSettings().update(data);
                networkDirty();
                markDirtyQuick();
                return;
            }
        }
    }

    private void removeConnector(int channel, SidedPos pos) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        ConsumerId consumerId = worldBlob.getConsumerAt(pos.getPos().offset(pos.getSide()));
        SidedConsumer toremove = null;
        for (Map.Entry<SidedConsumer, ConnectorInfo> entry : channels[channel].getConnectors().entrySet()) {
            SidedConsumer key = entry.getKey();
            if (key.getConsumerId().equals(consumerId)) {
                toremove = key;
                break;
            }
        }
        if (toremove != null) {
            channels[channel].getConnectors().remove(toremove);
            networkDirty();
            markDirtyQuick();
        }
    }

    private void createConnector(int channel, SidedPos pos) {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        BlockPos consumerPos = pos.getPos().offset(pos.getSide());
        ConsumerId consumerId = worldBlob.getConsumerAt(consumerPos);
        if (consumerId == null) {
            throw new RuntimeException("What?");
        }
        SidedConsumer id = new SidedConsumer(consumerId, pos.getSide().getOpposite());
        boolean advanced = getWorld().getBlockState(consumerPos).getBlock() == NetCableSetup.advancedConnectorBlock;
        channels[channel].createConnector(id, advanced);
        networkDirty();
        markDirtyQuick();
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
            SidedPos pos = new SidedPos(args.get("pos").getCoordinate(), EnumFacing.VALUES[args.get("side").getInteger()]);
            createConnector(channel, pos);
            return true;
        } else if (CMD_REMOVECHANNEL.equals(command)) {
            int index = args.get("index").getInteger();
            removeChannel(index);
            return true;
        } else if (CMD_REMOVECONNECTOR.equals(command)) {
            SidedPos pos = new SidedPos(args.get("pos").getCoordinate(), EnumFacing.VALUES[args.get("side").getInteger()]);
            int channel = args.get("channel").getInteger();
            removeConnector(channel, pos);
            return true;
        } else if (CMD_UPDATECONNECTOR.equals(command)) {
            SidedPos pos = new SidedPos(args.get("pos").getCoordinate(), EnumFacing.VALUES[args.get("side").getInteger()]);
            int channel = args.get("channel").getInteger();
            updateConnector(channel, pos, args);
            return true;
        } else if (CMD_UPDATECHANNEL.equals(command)) {
            int channel = args.get("channel").getInteger();
            updateChannel(channel, args);
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
