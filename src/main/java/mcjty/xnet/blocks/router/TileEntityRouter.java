package mcjty.xnet.blocks.router;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.clientinfo.ControllerChannelClientInfo;
import mcjty.xnet.logic.ChannelInfo;
import mcjty.xnet.logic.LogicTools;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public final class TileEntityRouter extends GenericTileEntity {

    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";
    public static final String CMD_GETREMOTECHANNELS = "getRemoteChannelInfo";
    public static final String CLIENTCMD_CHANNELSREMOTEREADY = "channelsRemoteReady";

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
        LogicTools.connectors(getWorld(), getPos())
                .map(connectorPos -> LogicTools.getControllerForConnector(getWorld(), connectorPos))
                .forEach(controller -> {
                    for (int i = 0; i < MAX_CHANNELS; i++) {
                        ChannelInfo channelInfo = controller.getChannels()[i];
                        if (channelInfo != null && !channelInfo.getChannelName().isEmpty()) {
                            ControllerChannelClientInfo ci = new ControllerChannelClientInfo(channelInfo.getChannelName(), controller.getPos(), channelInfo.getType(), i);
                            list.add(ci);
                        }
                    }
                });

        return list;
    }

    @Nonnull
    private List<ControllerChannelClientInfo> findRemoteChannelInfo() {
        List<ControllerChannelClientInfo> list = new ArrayList<>();
        NetworkId networkId = findAdvancedNetwork();
        if (networkId != null) {
            LogicTools.consumers(getWorld(), networkId)
                    .forEach(consumerPos -> LogicTools.routers(getWorld(), consumerPos)
                            .filter(r -> r != this)
                            .forEach(router -> list.addAll(router.findChannelInfo())));
        }
        return list;
    }

    @Nullable
    private NetworkId findAdvancedNetwork() {
        WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
        return LogicTools.advancedConnectors(getWorld(), getPos())
                .findFirst()
                .map(worldBlob::getNetworkAt)
                .orElse(null);
    }

    public void addRoutedConnectors(Map<SidedConsumer, IConnectorSettings> connectors, String channelName, IChannelType type) {
        NetworkId networkId = findAdvancedNetwork();
        if (networkId != null) {
            LogicTools.consumers(getWorld(), networkId)
                    .forEach(consumerPos -> LogicTools.routers(getWorld(), consumerPos)
//                            .filter(r -> r != this)
                            .forEach(router -> router.addConnectorsFromConnectedNetworks(connectors, channelName, type)));
        }
    }

    private void addConnectorsFromConnectedNetworks(Map<SidedConsumer, IConnectorSettings> connectors, String channelName, IChannelType type) {
        LogicTools.connectors(getWorld(), getPos())
                .map(connectorPos -> LogicTools.getControllerForConnector(getWorld(), connectorPos))
                .filter(Objects::nonNull)
                .forEach(controller -> {
                    for (int i = 0; i < MAX_CHANNELS; i++) {
                        ChannelInfo info = controller.getChannels()[i];
                        if (info != null && channelName.equals(info.getChannelName()) && type.equals(info.getType())) {
                            connectors.putAll(controller.getConnectors(i));
                        }
                    }
                });
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
