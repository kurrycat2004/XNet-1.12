package mcjty.xnet.init;

import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.ControllerBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ControllerBlock controllerBlock;

    public static void init() {
        controllerBlock = new ControllerBlock();
        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
        NetCableSetup.initClient();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        NetCableSetup.initItemModels();
    }
}
