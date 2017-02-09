package mcjty.xnet.blocks.controller;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.gui.widgets.*;
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
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiController extends GenericGuiContainer<TileEntityController> {
    private WidgetList list;
    private int listDirty;

    // A copy of the consumers we're currently showing
    private List<BlockPos> consumers = null;

    // From server.
    public static List<BlockPos> fromServer_consumers = null;


    public GuiController(TileEntityController controller, EmptyContainer container) {
        super(XNet.instance, XNetMessages.INSTANCE, controller, container, GuiProxy.GUI_MANUAL_MAIN, "controller");

        xSize = 256;
        ySize = 180;
    }

    @Override
    public void initGui() {
        super.initGui();

        list = new WidgetList(mc, this).addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
//                setSelectedBlock(index);
            }
        });
        Slider listSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(list);
        Panel listPanel = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(3).setSpacing(1)).addChild(list).addChild(listSlider);

        Widget toplevel = new Panel(mc, this).setFilledRectThickness(2).setLayout(new VerticalLayout()).addChild(listPanel);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));
        window = new Window(this, toplevel);

        fromServer_consumers = null;
        listDirty = 0;
        XNetMessages.INSTANCE.sendToServer(new PacketGetConsumers(tileEntity.getPos()));
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

            Panel panel = new Panel(mc, this).setLayout(new HorizontalLayout());
            panel.addChild(new BlockRender(mc, this).setRenderItem(block));
//            panel.addChild(new Label(mc, this).setText(displayName).setColor(color).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setDesiredWidth(90));
            panel.addChild(new Label(mc, this).setDynamic(true).setText(BlockPosTools.toString(coordinate)).setColor(color));
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
