package mcjty.xnet.init;

import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.builder.BlockFlags;
import mcjty.lib.builder.GenericBlockBuilderFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.blocks.controller.gui.GuiController;
import mcjty.xnet.blocks.facade.FacadeBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyBlock;
import mcjty.xnet.blocks.redstoneproxy.RedstoneProxyUBlock;
import mcjty.xnet.blocks.router.GuiRouter;
import mcjty.xnet.blocks.router.TileEntityRouter;
import mcjty.xnet.blocks.wireless.TileEntityWirelessRouter;
import mcjty.xnet.gui.GuiProxy;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static GenericBlock<TileEntityController, GenericContainer> controllerBlock;
    public static GenericBlock<TileEntityRouter, GenericContainer> routerBlock;
    public static GenericBlock<TileEntityWirelessRouter, GenericContainer> wirelessRouterBlock;

    public static FacadeBlock facadeBlock;
    public static RedstoneProxyBlock redstoneProxyBlock;
    public static RedstoneProxyUBlock redstoneProxyUBlock;

    public static GenericBlockBuilderFactory builderFactory;

    public static void init() {
        builderFactory = new GenericBlockBuilderFactory(XNet.instance);

        facadeBlock = new FacadeBlock();
        redstoneProxyBlock = new RedstoneProxyBlock();
        redstoneProxyUBlock = new RedstoneProxyUBlock();

        controllerBlock = builderFactory.<TileEntityController> builder("controller")
                .tileEntityClass(TileEntityController.class)
                .container(TileEntityController.CONTAINER_FACTORY)
                .flags(BlockFlags.REDSTONE_CHECK)   // Not really for redstone check but to have TE.checkRedstone() being called
                .guiId(GuiProxy.GUI_CONTROLLER)
                .property(TileEntityController.ERROR)
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.controller")
                .build();
        routerBlock = builderFactory.<TileEntityRouter> builder("router")
                .tileEntityClass(TileEntityRouter.class)
                .emptyContainer()
                .guiId(GuiProxy.GUI_ROUTER)
                .property(TileEntityRouter.ERROR)
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.router")
                .build();
        wirelessRouterBlock = builderFactory.<TileEntityWirelessRouter> builder("wireless_router")
                .tileEntityClass(TileEntityWirelessRouter.class)
                .emptyContainer()
                .guiId(GuiProxy.GUI_WIRELESS_ROUTER)
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.wireless_router")
                .build();

        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
        controllerBlock.setGuiClass(GuiController.class);

        routerBlock.initModel();
        routerBlock.setGuiClass(GuiRouter.class);

        wirelessRouterBlock.initModel();
        wirelessRouterBlock.setGuiClass(null);  // @todo

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
