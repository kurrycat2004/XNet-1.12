package mcjty.xnet.init;

import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.ControllerBlock;
import mcjty.xnet.blocks.facade.FacadeBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyUBlock;
import mcjty.xnet.blocks.router.RouterBlock;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ControllerBlock controllerBlock;
    public static RouterBlock routerBlock;
    public static FacadeBlock facadeBlock;
    public static RedstoneProxyBlock redstoneProxyBlock;
    public static RedstoneProxyUBlock redstoneProxyUBlock;

    public static void init() {
        controllerBlock = new ControllerBlock();
        routerBlock = new RouterBlock();
        facadeBlock = new FacadeBlock();
        redstoneProxyBlock = new RedstoneProxyBlock();
        redstoneProxyUBlock = new RedstoneProxyUBlock();
        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
        routerBlock.initModel();
        facadeBlock.initModel();
        redstoneProxyBlock.initModel();
        redstoneProxyUBlock.initModel();
        NetCableSetup.initClient();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        facadeBlock.initItemModel();
        NetCableSetup.initItemModels();
    }

    public static void initColorHandlers(BlockColors blockColors) {
        facadeBlock.initColorHandler(blockColors);
        NetCableSetup.initColorHandlers(blockColors);
    }
}
