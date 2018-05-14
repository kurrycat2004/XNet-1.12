package mcjty.xnet.init;

import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.builder.GenericBlockBuilderFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.ControllerBlock;
import mcjty.xnet.blocks.facade.FacadeBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyUBlock;
import mcjty.xnet.blocks.router.GuiRouter;
import mcjty.xnet.blocks.router.TileEntityRouter;
import mcjty.xnet.gui.GuiProxy;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ControllerBlock controllerBlock;
    public static GenericBlock<TileEntityRouter, GenericContainer> routerBlock;
    public static FacadeBlock facadeBlock;
    public static RedstoneProxyBlock redstoneProxyBlock;
    public static RedstoneProxyUBlock redstoneProxyUBlock;

    public static GenericBlockBuilderFactory builderFactory;

    public static void init() {
        builderFactory = new GenericBlockBuilderFactory(XNet.instance);

        controllerBlock = new ControllerBlock();
        facadeBlock = new FacadeBlock();
        redstoneProxyBlock = new RedstoneProxyBlock();
        redstoneProxyUBlock = new RedstoneProxyUBlock();

        routerBlock = ModBlocks.builderFactory.<TileEntityRouter> builder("router")
                .tileEntityClass(TileEntityRouter.class)
                .emptyContainer()
                .guiId(GuiProxy.GUI_ROUTER)
                .property(TileEntityRouter.ERROR)
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.router")
                .build();

        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();

        routerBlock.initModel();
        routerBlock.setGuiClass(GuiRouter.class);

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

    @SideOnly(Side.CLIENT)
    public static void initColorHandlers(BlockColors blockColors) {
        facadeBlock.initColorHandler(blockColors);
        NetCableSetup.initColorHandlers(blockColors);
    }
}
