package mcjty.xnet.logic;

import mcjty.xnet.api.channels.IChannelType;

public class ChannelInfo {

    private final IChannelType type;

    public ChannelInfo(IChannelType type) {
        this.type = type;
    }

    public IChannelType getType() {
        return type;
    }

}
