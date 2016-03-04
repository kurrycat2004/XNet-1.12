package mcjty.xnet.init;

import mcjty.xnet.multipart.ItemConnectorPart;
import mcjty.xnet.multipart.RFConnectorPart;
import mcjty.xnet.multipart.XNetAdvancedCableMultiPart;
import mcjty.xnet.multipart.XNetCableMultiPart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static void init() {
        MultipartRegistry.registerPart(XNetCableMultiPart.class, "name");
        MultipartRegistry.registerPart(XNetAdvancedCableMultiPart.class, "name2");
        MultipartRegistry.registerPart(RFConnectorPart.class, "nametest2");
        MultipartRegistry.registerPart(ItemConnectorPart.class, "nametest3");
        //NetCableSetup.init();
    }

}
