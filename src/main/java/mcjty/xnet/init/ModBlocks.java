package mcjty.xnet.init;

import mcjty.xnet.cables.XNetAdvancedCableMultiPart;
import mcjty.xnet.cables.XNetCableMultiPart;
import mcjty.xnet.connectors.ItemConnectorPart;
import mcjty.xnet.connectors.RFConnectorPart;
import mcjty.xnet.terminal.TerminalPart;
import mcmultipart.multipart.MultipartRegistry;

public class ModBlocks {

    public static void init() {
        MultipartRegistry.registerPart(XNetCableMultiPart.class, "cable");
        MultipartRegistry.registerPart(XNetAdvancedCableMultiPart.class, "advanced_cable");
        MultipartRegistry.registerPart(RFConnectorPart.class, "rfconnector");
        MultipartRegistry.registerPart(ItemConnectorPart.class, "itemconnector");
        MultipartRegistry.registerPart(TerminalPart.class, "terminal");
        //NetCableSetup.init();
    }

}
