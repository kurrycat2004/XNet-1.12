package mcjty.xnet.blocks.router;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.BlockPosTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.logic.ControllerChannelClientInfo;
import mcjty.xnet.network.PacketGetChannelsRouter;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class GuiRouter extends GenericGuiContainer<TileEntityRouter> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/router.png");

    private WidgetList channelList;
    public static java.util.List<ControllerChannelClientInfo> fromServer_channels = null;
    private boolean needsRefresh = true;
    private int listDirty;

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

        Panel listPanel = initChannelListPanel();
        toplevel.addChild(listPanel);

        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);

        refresh();
        listDirty = 0;
    }

    public void refresh() {
        fromServer_channels = null;
        needsRefresh = true;
        listDirty = 3;
        requestListsIfNeeded();
    }


    private Panel initChannelListPanel() {
        channelList = new WidgetList(mc, this).addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
//                setSelectedBlock(index);
            }
        });
//        channelList.setPropagateEventsToChildren(true);
        Slider listSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(channelList);
        return new Panel(mc, this)
                .setLayout(new HorizontalLayout().setHorizontalMargin(3).setSpacing(1))
                .addChild(channelList)
                .addChild(listSlider)
                .setLayoutHint(new PositionalLayout.PositionalHint(2, 20, 169, 214));
    }

    private boolean listsReady() {
        return fromServer_channels != null;
    }

    private void populateList() {
        if (!listsReady()) {
            return;
        }
        if (!needsRefresh) {
            return;
        }
        needsRefresh = false;

        channelList.removeChildren();

        int sel = channelList.getSelected();

        for (ControllerChannelClientInfo channel : fromServer_channels) {
            String name = channel.getChannelName();
            BlockPos controllerPos = channel.getPos();
            IChannelType type = channel.getChannelType();
            int index = channel.getIndex();

            int color = StyleConfig.colorTextInListNormal;

            Panel panel = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0));
            panel.addChild(new Label<>(mc, this).setText(name + " (" + BlockPosTools.toString(controllerPos) + "): " + index + " (" +
                type.getName() + ")"));
            channelList.addChild(panel);
        }

        channelList.setSelected(sel);
    }

    private void requestListsIfNeeded() {
        if (fromServer_channels != null) {
            return;
        }
        listDirty--;
        if (listDirty <= 0) {
            XNetMessages.INSTANCE.sendToServer(new PacketGetChannelsRouter(tileEntity.getPos()));
            listDirty = 10;
        }
    }



    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        requestListsIfNeeded();
        populateList();
        drawWindow();
    }
}
