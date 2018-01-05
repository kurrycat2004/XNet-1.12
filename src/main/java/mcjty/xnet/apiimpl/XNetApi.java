package mcjty.xnet.apiimpl;

import mcjty.xnet.api.IXNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XNetApi implements IXNet {

    private final Map<String, IChannelType> channels = new HashMap<>();
    private final List<IConnectable> connectables = new ArrayList<>();

    @Override
    public void registerChannelType(IChannelType type) {
        channels.put(type.getID(), type);
    }

    @Override
    public void registerConnectable(@Nonnull IConnectable connectable) {
        connectables.add(connectable);
    }

    @Nullable
    public IChannelType findType(@Nonnull String id) {
        return channels.get(id);
    }

    public Map<String, IChannelType> getChannels() {
        return channels;
    }

    public List<IConnectable> getConnectables() {
        return connectables;
    }
}
