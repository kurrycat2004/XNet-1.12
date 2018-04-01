package mcjty.xnet.blocks.router;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.BlockPosTools;
import mcjty.typed.Type;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.clientinfo.ControllerChannelClientInfo;
import mcjty.xnet.config.GeneralConfiguration;
import mcjty.xnet.logic.ChannelInfo;
import mcjty.xnet.logic.LogicTools;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public final class TileEntityRouter extends GenericTileEntity {

    public static final String CMD_UPDATENAME = "updateName";
    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";
    public static final String CMD_GETREMOTECHANNELS = "getRemoteChannelInfo";
    public static final String CLIENTCMD_CHANNELSREMOTEREADY = "channelsRemoteReady";

    private Map<LocalChannelId, String> publishedChannels = new HashMap<>();
    private int channelCount = 0;

    public TileEntityRouter() {
    }

    public void addPublishedChannels(Set<String> channels) {
        channels.addAll(publishedChannels.values());
    }

    public int countPublishedChannelsOnNet() {
        Set<String> channels = new HashSet<>();
        NetworkId networkId = findRoutingNetwork();
        if (networkId != null) {
            LogicTools.routers(getWorld(), networkId)
                    .forEach(router -> router.addPublishedChannels(channels));
        }
        return channels.size();
    }

    public boolean inError() {
        return channelCount > GeneralConfiguration.maxPublishedChannels;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(int cnt) {
        if (channelCount == cnt) {
            return;
        }
        channelCount = cnt;
        markDirtyClient();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        boolean oldError = inError();

        super.onDataPacket(net, packet);

        if (getWorld().isRemote) {
            // If needed send a render update.
            if (oldError != inError()) {
                getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }
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
        tagCompound.setInteger("chancnt", channelCount);
        NBTTagList published = new NBTTagList();
        for (Map.Entry<LocalChannelId, String> entry : publishedChannels.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            BlockPosTools.writeToNBT(tc, "pos", entry.getKey().getControllerPos());
            tc.setInteger("index", entry.getKey().getIndex());
            tc.setString("name", entry.getValue());
            published.appendTag(tc);
        }
        tagCompound.setTag("published", published);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        channelCount = tagCompound.getInteger("chancnt");
        NBTTagList published = tagCompound.getTagList("published", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < published.tagCount() ; i++) {
            NBTTagCompound tc = published.getCompoundTagAt(i);
            LocalChannelId id = new LocalChannelId(BlockPosTools.readFromNBT(tc, "pos"), tc.getInteger("index"));
            String name = tc.getString("name");
            publishedChannels.put(id, name);
        }
    }

    @Nonnull
    private List<ControllerChannelClientInfo> findLocalChannelInfo(boolean onlyPublished) {
        List<ControllerChannelClientInfo> list = new ArrayList<>();
        LogicTools.connectors(getWorld(), getPos())
                .map(connectorPos -> LogicTools.getControllerForConnector(getWorld(), connectorPos))
                .filter(Objects::nonNull)
                .forEach(controller -> {
                    for (int i = 0; i < MAX_CHANNELS; i++) {
                        ChannelInfo channelInfo = controller.getChannels()[i];
                        if (channelInfo != null && !channelInfo.getChannelName().isEmpty()) {
                            LocalChannelId id = new LocalChannelId(controller.getPos(), i);
                            String publishedName = publishedChannels.get(id);
                            if (publishedName == null) {
                                publishedName = "";
                            }
                            if ((!onlyPublished) || !publishedName.isEmpty()) {
                                ControllerChannelClientInfo ci = new ControllerChannelClientInfo(channelInfo.getChannelName(), publishedName, controller.getPos(), channelInfo.getType(), i);
                                list.add(ci);
                            }
                        }
                    }
                });

        return list;
    }

    @Nonnull
    private List<ControllerChannelClientInfo> findRemoteChannelInfo() {
        List<ControllerChannelClientInfo> list = new ArrayList<>();
        NetworkId networkId = findRoutingNetwork();
        if (networkId != null) {
            LogicTools.consumers(getWorld(), networkId)
                    .forEach(consumerPos -> LogicTools.routers(getWorld(), consumerPos)
                            .filter(r -> r != this)
                            .forEach(router -> list.addAll(router.findLocalChannelInfo(true))));
        }
        return list;
    }

    @Nullable
    private NetworkId findRoutingNetwork() {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        return LogicTools.routingConnectors(getWorld(), getPos())
                .findFirst()
                .map(worldBlob::getNetworkAt)
                .orElse(null);
    }

    public void addRoutedConnectors(Map<SidedConsumer, IConnectorSettings> connectors, @Nonnull BlockPos controllerPos, int channel, IChannelType type) {
        if (inError()) {
            // We are in error. Don't do anything
            return;
        }
        LocalChannelId id = new LocalChannelId(controllerPos, channel);
        String publishedName = publishedChannels.get(id);
        if (publishedName != null && !publishedName.isEmpty()) {
            NetworkId networkId = findRoutingNetwork();
            if (networkId != null) {
                LogicTools.consumers(getWorld(), networkId)
                        .forEach(consumerPos -> LogicTools.routers(getWorld(), consumerPos)
                                .forEach(router -> router.addConnectorsFromConnectedNetworks(connectors, publishedName, type)));
            } else {
                // If there is no routing network that means we have a local network only
                addConnectorsFromConnectedNetworks(connectors, publishedName, type);
            }
        }
    }

    private void addConnectorsFromConnectedNetworks(Map<SidedConsumer, IConnectorSettings> connectors, String channelName, IChannelType type) {
        LogicTools.connectors(getWorld(), getPos())
                .map(connectorPos -> LogicTools.getControllerForConnector(getWorld(), connectorPos))
                .filter(Objects::nonNull)
                .forEach(controller -> {
                    for (int i = 0; i < MAX_CHANNELS; i++) {
                        ChannelInfo info = controller.getChannels()[i];
                        if (info != null) {
                            String publishedName = publishedChannels.get(new LocalChannelId(controller.getPos(), i));
                            if (publishedName != null && !publishedName.isEmpty()) {
                                if (channelName.equals(publishedName) && type.equals(info.getType())) {
                                    connectors.putAll(controller.getConnectors(i));
                                }
                            }
                        }
                    }
                });
    }

    private void updatePublishName(@Nonnull BlockPos controllerPos, int channel, String name) {
        LocalChannelId id = new LocalChannelId(controllerPos, channel);
        if (name == null || name.isEmpty()) {
            publishedChannels.remove(id);
        } else {
            publishedChannels.put(id, name);
        }
        int number = countPublishedChannelsOnNet();
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        NetworkId networkId = findRoutingNetwork();
        if (networkId != null) {
            if (number != channelCount) {
                LogicTools.routers(getWorld(), networkId)
                        .forEach(router -> router.setChannelCount(number));
            }
            worldBlob.incNetworkVersion(networkId); // Force a recalc of affected networks
        }
        for (NetworkId net : worldBlob.getNetworksAt(pos)) {
            worldBlob.incNetworkVersion(net);
        }
        for (EnumFacing facing : EnumFacing.VALUES) {
            for (NetworkId net : worldBlob.getNetworksAt(pos.offset(facing))) {
                worldBlob.incNetworkVersion(net);
            }
        }


        markDirtyQuick();
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_UPDATENAME.equals(command)) {
            BlockPos controllerPos = args.get("pos").getCoordinate();
            int channel = args.get("channel").getInteger();
            String name = args.get("name").getString();
            updatePublishName(controllerPos, channel, name);
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
            return type.convert(findLocalChannelInfo(false));
        } else if (CMD_GETREMOTECHANNELS.equals(command)) {
            return type.convert(findRemoteChannelInfo());
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
            GuiRouter.fromServer_localChannels = new ArrayList<>(Type.create(ControllerChannelClientInfo.class).convert(list));
            return true;
        } else if (CLIENTCMD_CHANNELSREMOTEREADY.equals(command)) {
            GuiRouter.fromServer_remoteChannels = new ArrayList<>(Type.create(ControllerChannelClientInfo.class).convert(list));
            return true;
        }
        return false;
    }
}
