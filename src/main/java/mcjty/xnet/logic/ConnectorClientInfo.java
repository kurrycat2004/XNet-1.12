package mcjty.xnet.logic;

import mcjty.xnet.multiblock.ConsumerId;

public class ConnectorClientInfo {
    private final SidedPos pos;
    private final ConsumerId consumerId;

    public ConnectorClientInfo(SidedPos pos, ConsumerId consumerId) {
        this.pos = pos;
        this.consumerId = consumerId;
    }

    public SidedPos getPos() {
        return pos;
    }

    public ConsumerId getConsumerId() {
        return consumerId;
    }
}
