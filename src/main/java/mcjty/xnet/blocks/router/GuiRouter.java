package mcjty.xnet.blocks.router;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Panel;
import mcjty.xnet.XNet;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiRouter extends GenericGuiContainer<TileEntityRouter> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/controller.png");


    public GuiRouter(TileEntityRouter router, EmptyContainer container) {
        super(XNet.instance, XNetMessages.INSTANCE, router, container, GuiProxy.GUI_MANUAL_MAIN, "router");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setBackground(mainBackground);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        drawWindow();
    }
}
