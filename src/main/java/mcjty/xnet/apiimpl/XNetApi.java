package mcjty.xnet.apiimpl;

import mcjty.xnet.api.IXNet;
import mcjty.xnet.api.channels.IChannelType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class XNetApi implements IXNet {

    private final Map<String, IChannelType> channels = new HashMap<>();

    @Override
    public void registerChannelType(IChannelType type) {
        channels.put(type.getID(), type);
    }

    @Nullable
    public IChannelType findType(@Nonnull String id) {
        return channels.get(id);
    }

    public Map<String, IChannelType> getChannels() {
        return channels;
    }
}
