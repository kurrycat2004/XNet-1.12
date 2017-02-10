package mcjty.xnet.logic;

import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectorSettings;
import net.minecraft.nbt.NBTTagCompound;

public class ConnectorInfo {

    private final IChannelType type;

    private IConnectorSettings connectorSettings;

    public ConnectorInfo(IChannelType type) {
        this.type = type;
        connectorSettings = type.createConnector();
    }

    public IChannelType getType() {
        return type;
    }

    public IConnectorSettings getConnectorSettings() {
        return connectorSettings;
    }

    public void setConnectorSettings(IConnectorSettings connectorSettings) {
        this.connectorSettings = connectorSettings;
    }


    public void writeToNBT(NBTTagCompound tag) {
        connectorSettings.writeToNBT(tag);
    }

    public void readFromNBT(NBTTagCompound tag) {
        connectorSettings.readFromNBT(tag);
    }
}
