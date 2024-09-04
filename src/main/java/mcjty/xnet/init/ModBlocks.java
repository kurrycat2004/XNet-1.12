package mcjty.xnet.init;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.builder.BaseBlockBuilder;
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
import mcjty.xnet.blocks.wireless.GuiWirelessRouter;
import mcjty.xnet.blocks.wireless.TileEntityWirelessRouter;
import mcjty.xnet.config.ConfigSetup;
import mcjty.xnet.setup.GuiProxy;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static GenericBlock<TileEntityController, GenericContainer> controllerBlock;
    public static GenericBlock<TileEntityRouter, GenericContainer> routerBlock;
    public static GenericBlock<TileEntityWirelessRouter, GenericContainer> wirelessRouterBlock;

    public static BaseBlock antennaBlock;
    public static BaseBlock antennaBaseBlock;
    public static BaseBlock antennaDishBlock;

    public static FacadeBlock facadeBlock;
    public static RedstoneProxyBlock redstoneProxyBlock;
    public static RedstoneProxyUBlock redstoneProxyUBlock;

    public static GenericBlockBuilderFactory builderFactory;

    private static final AxisAlignedBB ANTENA_BASE_BB, ANTENA_BASE_CBB, ANTENA_BB_X, ANTENA_BB_Z;

    public static void init() {
        builderFactory = new GenericBlockBuilderFactory(XNet.instance).creativeTabs(XNet.setup.getTab());

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
                .property(TileEntityWirelessRouter.ERROR)
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.wireless_router")
                .build();

        antennaBlock = new BaseBlockBuilder<>(XNet.instance, "antenna")
                .rotationType(BaseBlock.RotationType.HORIZROTATION)
                .flags(BlockFlags.NON_OPAQUE, BlockFlags.NON_FULLCUBE)
                .boundingBox((state, world, pos) -> state.getValue(BaseBlock.FACING_HORIZ).getAxis() == EnumFacing.Axis.X ? ANTENA_BB_X : ANTENA_BB_Z)
                .creativeTabs(XNet.setup.getTab())
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.antenna")
                .infoExtendedParameter(stack -> Integer.toString(ConfigSetup.antennaTier1Range.get()))
                .infoExtendedParameter(stack -> Integer.toString(ConfigSetup.wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_1].get()))
                .infoExtendedParameter(stack -> Integer.toString(ConfigSetup.antennaTier2Range.get()))
                .infoExtendedParameter(stack -> Integer.toString(ConfigSetup.wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_2].get()))
                .build();
        antennaBaseBlock = new BaseBlockBuilder<>(XNet.instance, "antenna_base")
                .rotationType(BaseBlock.RotationType.NONE)
                .flags(BlockFlags.NON_OPAQUE, BlockFlags.NON_FULLCUBE)
                .boundingBox((state, world, pos) -> ANTENA_BASE_BB)
                .addCollisionBoxToList((state, world, pos, entityBox, list, entity, b) -> {
                    AxisAlignedBB offset = ANTENA_BASE_CBB.offset(pos);
                    if (entityBox.intersects(offset)) list.add(offset);
                    return true;
                })
                .creativeTabs(XNet.setup.getTab())
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.antenna_base")
                .build();
        antennaDishBlock = new BaseBlockBuilder<>(XNet.instance, "antenna_dish")
                .rotationType(BaseBlock.RotationType.HORIZROTATION)
                .flags(BlockFlags.NON_OPAQUE)
                .creativeTabs(XNet.setup.getTab())
                .info("message.xnet.shiftmessage")
                .infoExtended("message.xnet.antenna_dish")
                .infoExtendedParameter(stack -> Integer.toString(ConfigSetup.wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_INF].get()))
                .build();

        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        controllerBlock.initModel();
        controllerBlock.setGuiFactory(GuiController::new);

        routerBlock.initModel();
        routerBlock.setGuiFactory(GuiRouter::new);

        wirelessRouterBlock.initModel();
        wirelessRouterBlock.setGuiFactory(GuiWirelessRouter::new);

        antennaBlock.initModel();
        antennaBaseBlock.initModel();
        antennaDishBlock.initModel();

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

    static {
        double a = 1D / 4;
        double aPix = 1D / 16;

        double aS = aPix * 7;
        double aE = 1D - aS;

        ANTENA_BASE_BB = new AxisAlignedBB(a, 0D, a, a * 3, 1D, a * 3);
        ANTENA_BASE_CBB = new AxisAlignedBB(aS, 0D, aS, aE, 1D, aE);
        ANTENA_BB_X = new AxisAlignedBB(aS, 0D, 0D, aE, 1D, 1D);
        ANTENA_BB_Z = new AxisAlignedBB(0D, 0D, aS, 1D, 1D, aE);
    }
}
