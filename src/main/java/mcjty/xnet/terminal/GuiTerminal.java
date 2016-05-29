package mcjty.xnet.terminal;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GuiItemScreen;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
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
import mcjty.xnet.network.PacketAddChannel;
import mcjty.xnet.network.PacketGetChannels;
import mcjty.xnet.network.PacketGetConnectors;
import mcjty.xnet.network.PacketRemoveChannel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class GuiTerminal extends GuiItemScreen {

    private static final ResourceLocation guielements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");

    private WidgetList channelList;
    private WidgetList connectorList;
    private TextField channelName;
    private Button plusButton;
    private Button minButton;

    private BlockPos pos;

    private int dirty = 10;
    public static java.util.List<BlockPos> devicesFromServer;
    public static java.util.List<String> channelsFromServer;

    public GuiTerminal(BlockPos pos) {
        super(XNet.instance, XNet.networkHandler.getNetworkWrapper(), 390, 230, GuiProxy.GUI_TERMINAL, "terminal");
        this.pos = pos;
    }

    @Override
    public void initGui() {
        super.initGui();

        Widget toplevel = new Panel(mc, this).setFilledRectThickness(2).setLayout(new VerticalLayout().setSpacing(1).setVerticalMargin(3))
                .addChild(createControlPanel())
                .addChild(createListPanel());
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
        devicesFromServer = null;
        channelsFromServer = null;
        dirty = 0;
        requestListsFromServer();
    }

    private Panel createControlPanel() {
        plusButton = new Button(mc, this).setText("+").setTooltips("Add a new channel").addButtonEvent(this::addChannel);
        minButton = new Button(mc, this).setText("-").setTooltips("Remove the selected channel").addButtonEvent(this::removeChannel);
        channelName = new TextField(mc, this).setTooltips("Name of the channel").setDesiredHeight(16);
        return new Panel(mc, this)./*setFilledRectThickness(2).*/setLayout(new HorizontalLayout()).setDesiredHeight(23).addChild(plusButton).addChild(minButton).addChild(channelName);
    }

    private Panel createListPanel() {
        channelList = new WidgetList(mc, this).addSelectionEvent(new SelectionEvent() {
            @Override
            public void select(Widget widget, int i) {
                setNameFromChannel();
            }

            @Override
            public void doubleClick(Widget widget, int i) {

            }
        });
        Slider channelSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(channelList);
        Panel channelPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(1).setHorizontalMargin(3)).addChild(channelList).addChild(channelSlider);

        connectorList = new WidgetList(mc, this);
        Slider connectorSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(connectorList);
        Panel connectorPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(1).setHorizontalMargin(3)).addChild(connectorList).addChild(connectorSlider);

        return new Panel(mc, this).setLayout(new HorizontalLayout().setSpacing(1).setHorizontalMargin(3)).addChild(channelPanel).addChild(connectorPanel);
    }

    private void setNameFromChannel() {
        int selected = channelList.getSelected();
        if (selected != -1) {
            Widget child = channelList.getChild(selected);
            String name = (String) child.getUserObject();
            channelName.setText(name);
        }

    }

    private void removeChannel(Widget w) {
        int selected = channelList.getSelected();
        if (selected != -1) {
            Widget child = channelList.getChild(selected);
            String name = (String) child.getUserObject();
            XNet.networkHandler.getNetworkWrapper().sendToServer(new PacketRemoveChannel(pos, name));
        }
    }

    private void addChannel(Widget w) {
        String name = channelName.getText();
        XNet.networkHandler.getNetworkWrapper().sendToServer(new PacketAddChannel(pos, name));
    }

    private void populateLists() {
        requestListsFromServer();

        channelList.removeChildren();
        if (channelsFromServer != null) {
            for (String name : channelsFromServer) {
                channelList.addChild(getChannelLine(name, false));
            }
        }

        connectorList.removeChildren();
        if (devicesFromServer != null) {
            for (BlockPos pos : devicesFromServer) {
                WorldClient world = Minecraft.getMinecraft().theWorld;
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                ItemStack stack = block.getPickBlock(state, null, world, pos, Minecraft.getMinecraft().thePlayer);
                if (stack != null) {
                    connectorList.addChild(getConnectorLine(stack, false));
                }
            }
        }
    }

    private void requestListsFromServer() {
        if (dirty > 0) {
            dirty--;
            return;
        }
        dirty = 10;
        XNet.networkHandler.getNetworkWrapper().sendToServer(new PacketGetConnectors(pos));
        XNet.networkHandler.getNetworkWrapper().sendToServer(new PacketGetChannels(pos));
    }


    private Panel getChannelLine(String name, boolean energy) {
        Panel panel = new Panel(mc,this).setLayout(new HorizontalLayout()).setDesiredHeight(16);
        panel.setUserObject(name);
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

    private void enableButtons() {
        int selected = channelList.getSelected();
        minButton.setEnabled(selected != -1);
        String name = channelName.getText();
        plusButton.setEnabled(!name.isEmpty());
    }

    @Override
    public void drawScreen(int xSize_lo, int ySize_lo, float par3) {
        super.drawScreen(xSize_lo, ySize_lo, par3);
        populateLists();
        enableButtons();
        drawWindow();
    }

}
