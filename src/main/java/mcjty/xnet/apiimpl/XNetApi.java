package mcjty.xnet.apiimpl;

import mcjty.xnet.api.IXNet;
import mcjty.xnet.api.channels.IChannelType;

import java.util.HashMap;
import java.util.Map;

public class XNetApi implements IXNet {

    private final Map<String, IChannelType> channels = new HashMap<>();

    @Override
    public void registerChannelType(IChannelType type) {
        channels.put(type.getID(), type);
    }
}
