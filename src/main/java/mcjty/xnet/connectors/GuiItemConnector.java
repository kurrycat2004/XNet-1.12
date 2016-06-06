package mcjty.xnet.connectors;

import mcjty.lib.gui.GuiItemScreen;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.xnet.XNet;
import mcjty.xnet.client.GuiProxy;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class GuiItemConnector extends GuiItemScreen {

    private BlockPos pos;

    public GuiItemConnector(BlockPos pos) {
        super(XNet.instance, XNet.networkHandler.getNetworkWrapper(), 390, 230, GuiProxy.GUI_ITEMCONNECTOR, "itemconnector");
        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();

        Widget toplevel = new Panel(mc, this).setFilledRectThickness(2).setLayout(new VerticalLayout().setSpacing(1).setVerticalMargin(3));
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new mcjty.lib.gui.Window(this, toplevel);
    }

    @Override
    public void drawScreen(int xSize_lo, int ySize_lo, float par3) {
        super.drawScreen(xSize_lo, ySize_lo, par3);
        drawWindow();
    }
}
