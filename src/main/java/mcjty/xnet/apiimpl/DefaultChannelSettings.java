package mcjty.xnet.apiimpl;

import mcjty.xnet.api.channels.RSMode;
import mcjty.xnet.blocks.cables.ConnectorTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DefaultChannelSettings {

    protected boolean checkRedstone(World world, AbstractConnectorSettings settings, BlockPos extractorPos) {
        RSMode rsMode = settings.getRsMode();
        if (rsMode != RSMode.IGNORED) {
            ConnectorTileEntity connector = (ConnectorTileEntity) world.getTileEntity(extractorPos);
            if (rsMode == RSMode.PULSE) {
                int prevPulse = settings.getPrevPulse();
                settings.setPrevPulse(connector.getPulseCounter());
                if (prevPulse == connector.getPulseCounter()) {
                    return true;
                }
            } else if ((rsMode == RSMode.ON) != (connector.getPowerLevel() > 0)) {
                return true;
            }
        }
        return false;
    }

}
