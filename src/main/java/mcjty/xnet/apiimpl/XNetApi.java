package mcjty.xnet.apiimpl;

import mcjty.xnet.api.IXNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class XNetApi implements IXNet {

    private final Map<String, IChannelType> channels = new HashMap<>();
    private final Map<ResourceLocation, IConnectable> connectables = new HashMap<>();

    @Override
    public void registerChannelType(IChannelType type) {
        channels.put(type.getID(), type);
    }

    @Override
    public void registerConnectable(@Nonnull ResourceLocation blockId, @Nonnull IConnectable connectable) {
        connectables.put(blockId, connectable);
    }

    @Nullable
    public IChannelType findType(@Nonnull String id) {
        return channels.get(id);
    }

    public Map<String, IChannelType> getChannels() {
        return channels;
    }

    @Nullable
    public IConnectable getConnectable(@Nonnull ResourceLocation blockId) {
        return connectables.get(blockId);
    }
}
