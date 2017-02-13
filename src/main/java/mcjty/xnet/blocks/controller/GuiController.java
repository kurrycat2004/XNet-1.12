package mcjty.xnet.blocks.controller;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.entity.GenericEnergyStorageTileEntity;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.RedstoneMode;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.logic.*;
import mcjty.xnet.network.PacketGetChannels;
import mcjty.xnet.network.PacketGetConnectedBlocks;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public class GuiController extends GenericGuiContainer<TileEntityController> {

    public static final int SIDEWIDTH = 80;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private WidgetList list;
    private int listDirty;

    private Panel channelEditPanel;
    private Panel connectorEditPanel;

    private ToggleButton channelButtons[] = new ToggleButton[MAX_CHANNELS];

    private SidedPos editingConnector = null;
    private int editingChannel = -1;

    private int showingChannel = -1;
    private SidedPos showingConnector = null;


    private EnergyBar energyBar;
    private ImageChoiceLabel redstoneMode;

    private static final ResourceLocation iconGuiElements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");
    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/controller.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(XNet.MODID, "textures/gui/sidegui.png");

    // From server.
    public static List<ChannelClientInfo> fromServer_channels = null;
    public static List<ConnectedBlockClientInfo> fromServer_connectedBlocks = null;
    private boolean needsRefresh = true;

    public GuiController(TileEntityController controller, ControllerContainer container) {
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

        initRedstoneMode();
        initEnergyBar();
        Panel listPanel = initConnectorListPanel();
        Panel channelSelectionPanel = initChannelSelectionPanel();
        initEditPanels();

        toplevel.addChild(channelSelectionPanel).addChild(listPanel).addChild(channelEditPanel).addChild(connectorEditPanel)
            .addChild(energyBar);

        window = new Window(this, toplevel);

        editingConnector = null;
        editingChannel = -1;

        refresh();
        listDirty = 0;
    }

    private void initRedstoneMode() {
        // Redstone mode is only put on gui in some cases
        redstoneMode = new ImageChoiceLabel(mc, this).
//                addChoiceEvent((parent, newChoice) -> changeRedstoneMode()).
                addChoice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", iconGuiElements, 0, 0).
                addChoice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", iconGuiElements, 16, 0).
                addChoice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", iconGuiElements, 32, 0);
        redstoneMode.setLayoutHint(new PositionalLayout.PositionalHint(66, 2, 16, 16));
    }

    private void initEnergyBar() {
        int maxEnergyStored = tileEntity.getMaxEnergyStored(EnumFacing.DOWN);
        energyBar = new EnergyBar(mc, this)
                .setHorizontal()
                .setMaxValue(maxEnergyStored)
                .setLayoutHint(new PositionalLayout.PositionalHint(5, 7, 35, 11))
                .setShowText(false);
        energyBar.setValue(GenericEnergyStorageTileEntity.getCurrentRF());
    }

    private void initEditPanels() {
        channelEditPanel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground)
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 5, 161, 52));
        connectorEditPanel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground)
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 60, 161, 52));
    }

    private Panel initConnectorListPanel() {
        list = new WidgetList(mc, this).addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
//                setSelectedBlock(index);
            }
        });
        list.setPropagateEventsToChildren(true);
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
        for (int i = 0 ; i < MAX_CHANNELS ; i++) {
            String channel = String.valueOf(i + 1);
            channelButtons[i] = new ToggleButton(mc, this).setDesiredWidth(14)
                    .setText(channel)
                    .setTooltips("Edit channel " + channel);
            int finalI = i;
            channelButtons[i].addButtonEvent(parent -> {
                selectChannelEditor(finalI);
            });
            channelSelectionPanel.addChild(channelButtons[i]);
        }
        return channelSelectionPanel;
    }

    private void selectChannelEditor(int finalI) {
        editingChannel = -1;
        for (int j = 0 ; j < MAX_CHANNELS ; j++) {
            if (j != finalI) {
                channelButtons[j].setPressed(false);
                editingChannel = finalI;
            }
        }
    }

    private void createConnector(SidedPos sidedPos) {
        sendServerCommand(XNetMessages.INSTANCE, TileEntityController.CMD_CREATECONNECTOR,
                new Argument("channel", getSelectedChannel()),
                new Argument("pos", sidedPos.getPos()),
                new Argument("side", sidedPos.getSide().ordinal()));
        refresh();
    }

    private void createChannel(String typeId) {
        sendServerCommand(XNetMessages.INSTANCE, TileEntityController.CMD_CREATECHANNEL,
                new Argument("index", getSelectedChannel()),
                new Argument("type", typeId));
        refresh();
    }

    private void refresh() {
        fromServer_channels = null;
        fromServer_connectedBlocks = null;
        showingChannel = -1;
        showingConnector = null;
        needsRefresh = true;
        listDirty = 3;
        requestListsIfNeeded();
    }

    private void selectConnectorEditor(SidedPos sidedPos, ToggleButton but, int finalI) {
        if (but.isPressed()) {
            editingConnector = sidedPos;
            selectChannelEditor(finalI);
        } else {
            editingConnector = null;
        }
        for (int i = 0 ; i < list.getChildCount() ; i++) {
            Panel p = (Panel) list.getChild(i);
            for (int j = 0 ; j < p.getChildCount() ; j++) {
                Widget w = p.getChild(j);
                if (w instanceof ToggleButton && w != but) {
                    but.setPressed(false);
                }
            }
        }
    }

    private void refreshChannelEditor() {
        if (!listsReady()) {
            return;
        }
        if (editingChannel != -1 && showingChannel != editingChannel) {
            showingChannel = editingChannel;
            channelButtons[editingChannel].setPressed(true);

            channelEditPanel.removeChildren();
            if (channelButtons[editingChannel].isPressed()) {
                ChannelClientInfo info = fromServer_channels.get(editingChannel);
                if (info != null) {
                    Widget label = new Label(mc, this).setText("Channel " + (editingChannel + 1))
                            .setLayoutHint(new PositionalLayout.PositionalHint(4, 3, 60, 14));
                    ChoiceLabel type = new ChoiceLabel(mc, this).addChoices("Item", "Energy", "Fluid")
                            .setLayoutHint(new PositionalLayout.PositionalHint(66, 3, 60, 14))
                            .setChoice(info.getType().getName());
                    ToggleButton enabled = new ToggleButton(mc, this).setCheckMarker(true).setPressed(true)
                            .setLayoutHint(new PositionalLayout.PositionalHint(130, 3, 13, 14));
                    ChoiceLabel mode = new ChoiceLabel(mc, this).addChoices("Round Robin", "Random", "First")
                            .setLayoutHint(new PositionalLayout.PositionalHint(4, 20, 100, 14));
                    channelEditPanel.addChild(label).addChild(enabled).addChild(type).addChild(mode);
                } else {
                    ChoiceLabel type = new ChoiceLabel(mc, this)
                            .setLayoutHint(new PositionalLayout.PositionalHint(20, 20, 60, 14));
                    for (IChannelType channelType : XNet.xNetApi.getChannels().values()) {
                        type.addChoices(channelType.getID());       // Show names?
                    }
                    Button create = new Button(mc, this)
                            .setText("Create")
                            .setLayoutHint(new PositionalLayout.PositionalHint(85, 20, 60, 14))
                            .addButtonEvent(parent -> createChannel(type.getCurrentChoice()));
                    channelEditPanel.addChild(type).addChild(create);
                }
            }
        } else if (showingChannel != -1 && editingChannel == -1) {
            showingChannel = -1;
            channelEditPanel.removeChildren();
        }
    }

    private ConnectorClientInfo findClientInfo(ChannelClientInfo info, SidedPos p) {
        for (ConnectorClientInfo connector : info.getConnectors().values()) {
            if (connector.getPos().equals(p)) {
                return connector;
            }
        }
        return null;
    }

    private ConnectorInfo findConnectorInfo(ChannelInfo info, ConnectorClientInfo clientInfo) {
        EnumFacing side = clientInfo.getPos().getSide();
        SidedConsumer sidedConsumer = new SidedConsumer(clientInfo.getConsumerId(), side.getOpposite());
        return info.getConnectors().get(sidedConsumer);
    }

    private void refreshConnectorEditor() {
        if (!listsReady()) {
            return;
        }
        if (editingConnector != null && !editingConnector.equals(showingConnector)) {
            showingConnector = editingConnector;
            connectorEditPanel.removeChildren();
            ChannelClientInfo info = fromServer_channels.get(editingChannel);
            if (info != null) {
                ConnectorClientInfo clientInfo = findClientInfo(info, editingConnector);
                if (clientInfo != null) {
                    EnumFacing side = clientInfo.getPos().getSide();
                    SidedConsumer sidedConsumer = new SidedConsumer(clientInfo.getConsumerId(), side.getOpposite());
                    ConnectorClientInfo connectorInfo = info.getConnectors().get(sidedConsumer);

                    ChoiceLabel type = new ChoiceLabel(mc, this).addChoices("Insert", "Extract")
                            .setLayoutHint(new PositionalLayout.PositionalHint(4, 3, 60, 14));

                    Widget label1 = new Label(mc, this).setText("Filter:").setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT)
                            .setLayoutHint(new PositionalLayout.PositionalHint(4, 35, 40, 14));
                    ToggleButton oreDict = new ToggleButton(mc, this).setText("Ore").setCheckMarker(true)
                            .setLayoutHint(new PositionalLayout.PositionalHint(46, 35, 40, 14));
                    ToggleButton meta = new ToggleButton(mc, this).setText("Meta").setCheckMarker(true)
                            .setLayoutHint(new PositionalLayout.PositionalHint(88, 35, 40, 14));

                    Widget label2 = new Label(mc, this).setText("Speed:").setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT)
                            .setLayoutHint(new PositionalLayout.PositionalHint(4, 20, 40, 14));
                    ChoiceLabel speed = new ChoiceLabel(mc, this).addChoices("1 item", "stack")
                            .setLayoutHint(new PositionalLayout.PositionalHint(46, 20, 50, 14));

                    connectorEditPanel.addChild(type).addChild(redstoneMode).addChild(label1).addChild(oreDict).addChild(meta)
                            .addChild(label2).addChild(speed);
                } else {
                    Button create = new Button(mc, this)
                            .setText("Create")
                            .setLayoutHint(new PositionalLayout.PositionalHint(85, 20, 60, 14))
                            .addButtonEvent(parent -> createConnector(editingConnector));
                    connectorEditPanel.addChild(create);
                }
            }
        } else if (showingConnector != null && editingConnector == null) {
            showingConnector = null;
            connectorEditPanel.removeChildren();
        }
    }



    private void requestListsIfNeeded() {
        if (fromServer_channels != null && fromServer_connectedBlocks != null) {
            return;
        }
        listDirty--;
        if (listDirty <= 0) {
            XNetMessages.INSTANCE.sendToServer(new PacketGetChannels(tileEntity.getPos()));
            XNetMessages.INSTANCE.sendToServer(new PacketGetConnectedBlocks(tileEntity.getPos()));
            listDirty = 10;
            showingChannel = -1;
            showingConnector = null;
        }
    }

    private int getSelectedChannel() {
        for (int i = 0 ; i < MAX_CHANNELS ; i++) {
            if (channelButtons[i].isPressed()) {
                return i;
            }
        }
        return -1;
    }

    private void populateList() {
        if (!listsReady()) {
            return;
        }
        if (!needsRefresh) {
            return;
        }
        needsRefresh = false;

        list.removeChildren();

        int index = 0;
        int sel = -1;
        BlockPos prevPos = null;

        for (ConnectedBlockClientInfo connectedBlock : fromServer_connectedBlocks) {
            SidedPos sidedPos = connectedBlock.getPos();
            BlockPos coordinate = sidedPos.getPos();

            int color = StyleConfig.colorTextInListNormal;

            Panel panel = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0));
            if (coordinate.equals(prevPos)) {
                panel.addChild(new BlockRender(mc, this));
            } else {
                panel.addChild(new BlockRender(mc, this).setRenderItem(connectedBlock.getConnectedBlock()));
                prevPos = coordinate;
            }
//            panel.addChild(new Label(mc, this).setText(displayName).setColor(color).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setDesiredWidth(90));
            panel.addChild(new Label(mc, this).setText(sidedPos.getSide().getName().substring(0, 1).toUpperCase()).setColor(color).setDesiredWidth(18));
            for (int i = 0 ; i < MAX_CHANNELS ; i++) {
                ToggleButton but = new ToggleButton(mc, this).setDesiredWidth(14);
                ChannelClientInfo info = fromServer_channels.get(i);
                if (info != null) {
                    ConnectorClientInfo clientInfo = findClientInfo(info, sidedPos);
                    if (clientInfo != null) {
                        but.setText("I");
                    }
                }
                but.setPressed(editingChannel == i && sidedPos.equals(editingConnector));
                int finalI = i;
                but.addButtonEvent(parent -> {
                    selectConnectorEditor(sidedPos, but, finalI);
                });
                panel.addChild(but);
            }
            list.addChild(panel);

            index++;
        }

        list.setSelected(sel);
    }

    private boolean listsReady() {
        return fromServer_channels != null && fromServer_connectedBlocks != null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int x1, int x2) {
        requestListsIfNeeded();
        populateList();
        refreshChannelEditor();
        refreshConnectorEditor();
        if (fromServer_channels != null) {
            for (int i = 0; i < MAX_CHANNELS; i++) {
                String channel = String.valueOf(i + 1);
                ChannelClientInfo info = fromServer_channels.get(i);
                if (info != null) {
                    channelButtons[i].setText(info.getType().getName().substring(0, 1).toUpperCase() + channel);
                } else {
                    channelButtons[i].setText(channel);
                }
            }
        }
        drawWindow();
        int channel = getSelectedChannel();
        if (channel != -1) {
            int x = (int) window.getToplevel().getBounds().getX();
            int y = (int) window.getToplevel().getBounds().getY();
            RenderHelper.drawVerticalGradientRect(x+channel * 14 + 41, y+22, x+channel * 14 + 41+12, y+230, 0x33aaffff, 0x33aaffff);
        }
        tileEntity.requestRfFromServer(XNet.MODID);
    }
}
