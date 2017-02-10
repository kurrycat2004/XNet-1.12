package mcjty.xnet.blocks.controller;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.tools.MinecraftTools;
import mcjty.lib.varia.BlockPosTools;
import mcjty.xnet.XNet;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.network.PacketGetConsumers;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiController extends GenericGuiContainer<TileEntityController> {

    public static final int SIDEWIDTH = 80;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private WidgetList list;
    private int listDirty;

    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/controller.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(XNet.MODID, "textures/gui/sidegui.png");

    // A copy of the consumers we're currently showing
    private List<BlockPos> consumers = null;

    // From server.
    public static List<BlockPos> fromServer_consumers = null;


    public GuiController(TileEntityController controller, EmptyContainer container) {
        super(XNet.instance, XNetMessages.INSTANCE, controller, container, GuiProxy.GUI_MANUAL_MAIN, "controller");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setBackgrounds(sideBackground, mainBackground)
                .setBackgroundLayout(true, SIDEWIDTH);
        toplevel.setBounds(new Rectangle(guiLeft-SIDEWIDTH, guiTop, xSize+SIDEWIDTH, ySize));

        Panel listPanel = initConsumerListPanel();
        Panel channelSelectionPanel = initChannelSelectionPanel();

        Panel channelEditPanel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground)
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 5, 161, 52));
        Panel consumerEditPanel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground)
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 60, 161, 52));

        toplevel.addChild(channelSelectionPanel);
        toplevel.addChild(listPanel);
        toplevel.addChild(channelEditPanel);
        toplevel.addChild(consumerEditPanel);

        window = new Window(this, toplevel);

        fromServer_consumers = null;
        listDirty = 0;
        XNetMessages.INSTANCE.sendToServer(new PacketGetConsumers(tileEntity.getPos()));
    }

    private Panel initConsumerListPanel() {
        list = new WidgetList(mc, this).addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
//                setSelectedBlock(index);
            }
        });
        Slider listSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(list);
        return new Panel(mc, this)
                .setLayout(new HorizontalLayout().setHorizontalMargin(3).setSpacing(1))
                .addChild(list)
                .addChild(listSlider)
                .setLayoutHint(new PositionalLayout.PositionalHint(2, 20, 169, 214));
    }

    private Panel initChannelSelectionPanel() {
        Panel channelSelectionPanel = new Panel(mc, this)
                .setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0))
                .setLayoutHint(new PositionalLayout.PositionalHint(41, 1, 124, 24));
        for (int i = 0 ; i < 8 ; i++) {
            String channel = String.valueOf(i + 1);
            ToggleButton but = new ToggleButton(mc, this).setDesiredWidth(14)
                    .setText(channel)
                    .setTooltips("Edit channel " + channel);
            channelSelectionPanel.addChild(but);
        }
        return channelSelectionPanel;
    }


    private void requestListsIfNeeded() {
        if (fromServer_consumers != null) {
            return;
        }
        listDirty--;
        if (listDirty <= 0) {
            XNetMessages.INSTANCE.sendToServer(new PacketGetConsumers(tileEntity.getPos()));
            listDirty = 10;
        }
    }


    private void populateList() {
        List<BlockPos> newConsumers = fromServer_consumers;
        if (newConsumers == null) {
            return;
        }
        if (newConsumers.equals(consumers)) {
            return;
        }


        consumers = new ArrayList<>(newConsumers);
        list.removeChildren();

        int index = 0;
        int sel = -1;
        for (BlockPos coordinate : consumers) {
            IBlockState state = MinecraftTools.getWorld(mc).getBlockState(coordinate);
            Block block = state.getBlock();

            int color = StyleConfig.colorTextInListNormal;

//            String displayName = BlockInfo.getReadableName(state);
//
//            if (coordinate.equals(tileEntity.getMonitor())) {
//                sel = index;
//                color = TEXT_COLOR_SELECTED;
//            }

            Panel panel = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0));
            panel.addChild(new BlockRender(mc, this).setRenderItem(block));
//            panel.addChild(new Label(mc, this).setText(displayName).setColor(color).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setDesiredWidth(90));
            panel.addChild(new Label(mc, this).setText("E").setColor(color).setDesiredWidth(18));
            for (int i = 0 ; i < 8 ; i++) {
                Button but = new Button(mc, this).setDesiredWidth(14);
                panel.addChild(but);
            }
            list.addChild(panel);

            index++;
        }

        list.setSelected(sel);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        requestListsIfNeeded();
        populateList();
        drawWindow();
    }
}
