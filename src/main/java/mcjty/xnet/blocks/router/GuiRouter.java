package mcjty.xnet.blocks.router;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.BlockPosTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.clientinfo.ControllerChannelClientInfo;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.network.PacketGetLocalChannelsRouter;
import mcjty.xnet.network.PacketGetRemoteChannelsRouter;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;

public class GuiRouter extends GenericGuiContainer<TileEntityRouter> {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/router.png");

    private WidgetList localChannelList;
    private WidgetList remoteChannelList;
    public static List<ControllerChannelClientInfo> fromServer_localChannels = null;
    public static List<ControllerChannelClientInfo> fromServer_remoteChannels = null;
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

        toplevel.addChild(initLocalChannelListPanel());
        toplevel.addChild(initRemoteChannelListPanel());
        toplevel.addChild(new Label<>(mc, this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setText("Local Channels").setLayoutHint(new PositionalLayout.PositionalHint(6,4,124,15)));
        toplevel.addChild(new Label<>(mc, this).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setText("Remote Channels").setLayoutHint(new PositionalLayout.PositionalHint(134,4,124,15)));

        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);

        refresh();
        listDirty = 0;
    }

    public void refresh() {
        fromServer_localChannels = null;
        fromServer_remoteChannels = null;
        needsRefresh = true;
        listDirty = 3;
        requestListsIfNeeded();
    }


    private Panel initLocalChannelListPanel() {
        localChannelList = new WidgetList(mc, this).addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
//                setSelectedBlock(index);
            }
        });
//        localChannelList.setPropagateEventsToChildren(true);
        Slider listSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(localChannelList);
        return new Panel(mc, this)
                .setLayout(new HorizontalLayout().setHorizontalMargin(3).setSpacing(1))
                .addChild(localChannelList)
                .addChild(listSlider)
                .setLayoutHint(new PositionalLayout.PositionalHint(2, 20, 124, 214));
    }

    private Panel initRemoteChannelListPanel() {
        remoteChannelList = new WidgetList(mc, this).addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
//                setSelectedBlock(index);
            }
        });
//        localChannelList.setPropagateEventsToChildren(true);
        Slider listSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollable(remoteChannelList);
        return new Panel(mc, this)
                .setLayout(new HorizontalLayout().setHorizontalMargin(3).setSpacing(1))
                .addChild(remoteChannelList)
                .addChild(listSlider)
                .setLayoutHint(new PositionalLayout.PositionalHint(130, 20, 126, 214));
    }

    private boolean listsReady() {
        return fromServer_localChannels != null && fromServer_remoteChannels != null;
    }

    private void populateList() {
        if (!listsReady()) {
            return;
        }
        if (!needsRefresh) {
            return;
        }
        needsRefresh = false;

        localChannelList.removeChildren();
        localChannelList.setRowheight(40);
        int sel = localChannelList.getSelected();

        for (ControllerChannelClientInfo channel : fromServer_localChannels) {
            localChannelList.addChild(makeChannelLine(channel));
        }

        localChannelList.setSelected(sel);

        remoteChannelList.removeChildren();
        remoteChannelList.setRowheight(40);
        sel = remoteChannelList.getSelected();

        for (ControllerChannelClientInfo channel : fromServer_remoteChannels) {
            remoteChannelList.addChild(makeChannelLine(channel));
        }

        remoteChannelList.setSelected(sel);
    }

    private Panel makeChannelLine(ControllerChannelClientInfo channel) {
        String name = channel.getChannelName();
        BlockPos controllerPos = channel.getPos();
        IChannelType type = channel.getChannelType();
        int index = channel.getIndex();

        Panel panel = new Panel(mc, this).setLayout(new PositionalLayout()).setDesiredHeight(30);
        Panel panel1 = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0)).setLayoutHint(new PositionalLayout.PositionalHint(0, 0, 120, 13));
        panel1.addChild(new Label<>(mc, this).setText("Channel:").setColor(0xff2244aa));
        panel1.addChild(new Label<>(mc, this).setText(name));

        Panel panel2 = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0)).setLayoutHint(new PositionalLayout.PositionalHint(0, 13, 120, 13));
        panel2.addChild(new Label<>(mc, this).setText("Pos:").setColor(0xff2244aa));
        panel2.addChild(new Label<>(mc, this).setText(BlockPosTools.toString(controllerPos)));

        Panel panel3 = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0)).setLayoutHint(new PositionalLayout.PositionalHint(0, 26, 120, 13));
        panel3.addChild(new Label<>(mc, this).setText("Index:").setColor(0xff2244aa));
        panel3.addChild(new Label<>(mc, this).setText(index + " (" + type.getName() + ")"));

        panel.addChild(panel1).addChild(panel2).addChild(panel3);
        return panel;
    }

    private void requestListsIfNeeded() {
        if (fromServer_localChannels != null && fromServer_remoteChannels != null) {
            return;
        }
        listDirty--;
        if (listDirty <= 0) {
            XNetMessages.INSTANCE.sendToServer(new PacketGetLocalChannelsRouter(tileEntity.getPos()));
            XNetMessages.INSTANCE.sendToServer(new PacketGetRemoteChannelsRouter(tileEntity.getPos()));
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
