package mcjty.xnet;

import mcjty.xnet.blocks.GenericCableISBM;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandlers {

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        Object object =  event.modelRegistry.getObject(GenericCableISBM.modelResourceLocation);
        if (object != null) {
            GenericCableISBM customModel = new GenericCableISBM();
            event.modelRegistry.putObject(GenericCableISBM.modelResourceLocation, customModel);
        }
    }

}
