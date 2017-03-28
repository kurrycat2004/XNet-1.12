package mcjty.xnet.init;

import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.ControllerBlock;
import mcjty.xnet.blocks.facade.FacadeBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyBlock;
import mcjty.xnet.blocks.router.RouterBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ControllerBlock controllerBlock;
    public static RouterBlock routerBlock;
    public static FacadeBlock facadeBlock;
    public static RedstoneProxyBlock redstoneProxyBlock;

    public static void init() {
        controllerBlock = new ControllerBlock();
        routerBlock = new RouterBlock();
        facadeBlock = new FacadeBlock();
        redstoneProxyBlock = new RedstoneProxyBlock();
        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
        routerBlock.initModel();
        facadeBlock.initModel();
        redstoneProxyBlock.initModel();
        NetCableSetup.initClient();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        facadeBlock.initItemModel();
        NetCableSetup.initItemModels();
    }
}
