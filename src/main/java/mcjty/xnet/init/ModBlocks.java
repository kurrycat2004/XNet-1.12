package mcjty.xnet.init;

import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.ControllerBlock;
import mcjty.xnet.blocks.facade.FacadeBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ControllerBlock controllerBlock;
    public static FacadeBlock facadeBlock;

    public static void init() {
        controllerBlock = new ControllerBlock();
        facadeBlock = new FacadeBlock();
        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
        facadeBlock.initModel();
        NetCableSetup.initClient();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        facadeBlock.initItemModel();
        NetCableSetup.initItemModels();
    }
}
