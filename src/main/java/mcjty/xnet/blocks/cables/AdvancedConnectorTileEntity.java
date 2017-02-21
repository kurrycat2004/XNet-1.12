package mcjty.xnet.blocks.cables;

import mcjty.xnet.config.GeneralConfiguration;

public class AdvancedConnectorTileEntity extends ConnectorTileEntity {

    @Override
    public int getMaxEnergy() {
        return GeneralConfiguration.maxRfAdvancedConnector;
    }
}
