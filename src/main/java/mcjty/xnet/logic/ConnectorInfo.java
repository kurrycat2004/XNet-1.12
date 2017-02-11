package mcjty.xnet.logic;

import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import net.minecraft.nbt.NBTTagCompound;

public class ConnectorInfo {

    private final IChannelType type;
    private final SidedConsumer id;
    private final IConnectorSettings connectorSettings;

    public ConnectorInfo(IChannelType type, SidedConsumer id) {
        this.type = type;
        this.id = id;
        connectorSettings = type.createConnector();
    }

    public IChannelType getType() {
        return type;
    }

    public IConnectorSettings getConnectorSettings() {
        return connectorSettings;
    }

    public SidedConsumer getId() {
        return id;
    }

    public void writeToNBT(NBTTagCompound tag) {
        connectorSettings.writeToNBT(tag);
    }

    public void readFromNBT(NBTTagCompound tag) {
        connectorSettings.readFromNBT(tag);
    }
}
