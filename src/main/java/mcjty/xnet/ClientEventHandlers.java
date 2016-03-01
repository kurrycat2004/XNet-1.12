package mcjty.xnet;

import mcjty.xnet.blocks.EnergyConnectorISBM;
import mcjty.xnet.blocks.NetCableISBM;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandlers {

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        Object object =  event.modelRegistry.getObject(NetCableISBM.modelResourceLocation);
        if (object != null) {
            NetCableISBM customModel = new NetCableISBM();
            event.modelRegistry.putObject(NetCableISBM.modelResourceLocation, customModel);
        }
        object =  event.modelRegistry.getObject(EnergyConnectorISBM.modelResourceLocation);
        if (object != null) {
            EnergyConnectorISBM customModel = new EnergyConnectorISBM();
            event.modelRegistry.putObject(EnergyConnectorISBM.modelResourceLocation, customModel);
        }
    }

}
