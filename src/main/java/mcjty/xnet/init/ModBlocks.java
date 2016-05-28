package mcjty.xnet.init;

import mcjty.xnet.blocks.controller.ControllerBlock;
import mcjty.xnet.cables.XNetAdvancedCableMultiPart;
import mcjty.xnet.cables.XNetCableMultiPart;
import mcjty.xnet.connectors.ItemConnectorPart;
import mcjty.xnet.connectors.RFConnectorPart;
import mcjty.xnet.terminal.TerminalPart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ControllerBlock controllerBlock;


    public static void init() {
        MultipartRegistry.registerPart(XNetCableMultiPart.class, "cable");
        MultipartRegistry.registerPart(XNetAdvancedCableMultiPart.class, "advanced_cable");
        MultipartRegistry.registerPart(RFConnectorPart.class, "rfconnector");
        MultipartRegistry.registerPart(ItemConnectorPart.class, "itemconnector");
        MultipartRegistry.registerPart(TerminalPart.class, "terminal");
        //NetCableSetup.init();

        controllerBlock = new ControllerBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
    }

}
