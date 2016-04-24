package mcjty.xnet.terminal;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GuiItemScreen;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.xnet.XNet;
import mcjty.xnet.client.GuiProxy;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiTerminal extends GuiItemScreen {

    private static final ResourceLocation guielements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");

    private WidgetList channelList;
    private WidgetList connectorList;

    public GuiTerminal() {
        super(XNet.instance, XNetMessages.INSTANCE, 390, 230, GuiProxy.GUI_TERMINAL, "terminal");
    }

    @Override
    public void initGui() {
        super.initGui();

        Widget toplevel = new Panel(mc, this).setFilledRectThickness(2).setLayout(new VerticalLayout().setSpacing(1).setVerticalMargin(3))
                .addChild(createControlPanel())
                .addChild(createListPanel());
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new mcjty.lib.gui.Window(this, toplevel);
    }

    private Panel createControlPanel() {
        Button plusButton = new Button(mc, this).setText("+").setTooltips("Add a new channel");
        Button minButton = new Button(mc, this).setText("-").setTooltips("Remove the selected channel");
        TextField channelName = new TextField(mc, this).setTooltips("Name of the channel").setDesiredHeight(16);
        return new Panel(mc, this)./*setFilledRectThickness(2).*/setLayout(new HorizontalLayout()).setDesiredHeight(23).addChild(plusButton).addChild(minButton).addChild(channelName);
    }

    private Panel createListPanel() {
        channelList = new WidgetList(mc, this);
        Slider channelSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(channelList);
        Panel channelPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(1).setHorizontalMargin(3)).addChild(channelList).addChild(channelSlider);

        connectorList = new WidgetList(mc, this);
        Slider connectorSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(connectorList);
        Panel connectorPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(1).setHorizontalMargin(3)).addChild(connectorList).addChild(connectorSlider);

        return new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(1).setHorizontalMargin(3)).addChild(channelPanel).addChild(connectorPanel);
    }

    private void populateLists() {
        channelList.removeChildren();
        channelList.addChild(getChannelLine("Quarry", false));
        channelList.addChild(getChannelLine("Main energy", true));

        connectorList.removeChildren();
        connectorList.addChild(getConnectorLine(new ItemStack(Blocks.CHEST, 0, 0), false));
        connectorList.addChild(getConnectorLine(new ItemStack(Block.REGISTRY.getObject(new ResourceLocation("rftools", "modular_storage")), 0, 0), false));
        connectorList.addChild(getConnectorLine(new ItemStack(Blocks.ENDER_CHEST, 0, 0), false));
        connectorList.addChild(getConnectorLine(new ItemStack(Blocks.FURNACE, 0, 0), true));
        connectorList.addChild(getConnectorLine(new ItemStack(Block.REGISTRY.getObject(new ResourceLocation("rftools", "builder")), 0, 0), true));
    }


    private Panel getChannelLine(String name, boolean energy) {
        Panel panel = new Panel(mc,this).setLayout(new HorizontalLayout()).setDesiredHeight(16);
        Label nameLabel = new Label(mc,this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setColor(StyleConfig.colorTextInListNormal);
        nameLabel.setText(name).setDesiredWidth(130);

        ImageLabel c = new ImageLabel(mc, this).setImage(guielements, energy ? 16 : 0, 48);
        c.setDesiredWidth(16).setDesiredHeight(16);

        panel.addChild(nameLabel);
        panel.addChild(c);
        return panel;
    }

    private Panel getConnectorLine(ItemStack stack, boolean energy) {
        Panel panel = new Panel(mc,this).setLayout(new HorizontalLayout()).setDesiredHeight(16);
        BlockRender blockRender = new BlockRender(mc, this).setRenderItem(stack).setOffsetX(-1).setOffsetY(-1);
        Label nameLabel = new Label(mc,this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setColor(StyleConfig.colorTextInListNormal);
        if (stack.getItem() == null) {
            nameLabel.setText("?").setDesiredWidth(112);
        } else {
            nameLabel.setText(stack.getDisplayName()).setDesiredWidth(112);
        }

        ImageLabel c = new ImageLabel(mc, this).setImage(guielements, energy ? 16 : 0, 48);
        c.setDesiredWidth(16).setDesiredHeight(16);

        panel.addChild(blockRender);
        panel.addChild(nameLabel);
        panel.addChild(c);
        return panel;
    }

    @Override
    public void drawScreen(int xSize_lo, int ySize_lo, float par3) {
        super.drawScreen(xSize_lo, ySize_lo, par3);
        populateLists();
        drawWindow();
    }

}
