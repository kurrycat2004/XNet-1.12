package mcjty.xnet.blocks.controller.gui;

import mcjty.lib.base.StyleConfig;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.tileentity.GenericEnergyStorageTileEntity;
import mcjty.lib.gui.RenderHelper;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.DefaultSelectionEvent;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.Logging;
import mcjty.xnet.XNet;
import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.api.keys.SidedPos;
import mcjty.xnet.blocks.controller.ControllerContainer;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.clientinfo.ChannelClientInfo;
import mcjty.xnet.clientinfo.ConnectedBlockClientInfo;
import mcjty.xnet.clientinfo.ConnectorClientInfo;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.network.PacketGetChannels;
import mcjty.xnet.network.PacketGetConnectedBlocks;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import java.awt.Rectangle;
import java.util.List;

import static mcjty.xnet.blocks.controller.TileEntityController.*;
import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public class GuiController extends GenericGuiContainer<TileEntityController> {

    public static final int SIDEWIDTH = 80;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    public static final String TAG_ENABLED = "enabled";
    public static final String TAG_NAME = "name";

    private WidgetList connectorList;
    private int listDirty;
    private TextField searchBar;

    private Panel channelEditPanel;
    private Panel connectorEditPanel;

    private ToggleButton channelButtons[] = new ToggleButton[MAX_CHANNELS];

    private SidedPos editingConnector = null;
    private int editingChannel = -1;

    private int showingChannel = -1;
    private SidedPos showingConnector = null;


    private EnergyBar energyBar;

    public static final ResourceLocation iconGuiElements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");
    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/controller.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(XNet.MODID, "textures/gui/sidegui.png");

    // From server.
    public static List<ChannelClientInfo> fromServer_channels = null;
    public static List<ConnectedBlockClientInfo> fromServer_connectedBlocks = null;
    private boolean needsRefresh = true;

    public GuiController(TileEntityController controller, ControllerContainer container) {
        super(XNet.instance, XNetMessages.INSTANCE, controller, container, GuiProxy.GUI_MANUAL_XNET, "controller");

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

        initEnergyBar();
        searchBar = new TextField(mc, this).setLayoutHint(new PositionalLayout.PositionalHint(5, 21, 163, 14));
        searchBar.addTextEvent((parent, newText) -> needsRefresh = true );
        Panel listPanel = initConnectorListPanel();
        Panel channelSelectionPanel = initChannelSelectionPanel();
        initEditPanels();

        toplevel.addChild(channelSelectionPanel).addChild(listPanel).addChild(channelEditPanel).addChild(connectorEditPanel)
                .addChild(searchBar)
                .addChild(energyBar);

        window = new Window(this, toplevel);

        editingConnector = null;
        editingChannel = -1;

        refresh();
        listDirty = 0;

        int currentRF = GenericEnergyStorageTileEntity.getCurrentRF();
        energyBar.setValue(currentRF);
        tileEntity.requestRfFromServer(XNet.MODID);
    }

    private void initEnergyBar() {
        int maxEnergyStored = tileEntity.getMaxEnergyStored();
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
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 5, 161, 37));
        connectorEditPanel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground)
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 45, 161, 108));
    }

    private Panel initConnectorListPanel() {
        connectorList = new WidgetList(mc, this).setName("connectors").addSelectionEvent(new DefaultSelectionEvent() {
            @Override
            public void select(Widget parent, int index) {
            }

            @Override
            public void doubleClick(Widget parent, int index) {
                hilightSelectedContainer(index);
            }
        });
        connectorList.setPropagateEventsToChildren(true);
        Slider listSlider = new Slider(mc, this).setDesiredWidth(10).setVertical().setScrollableName("connectors");
        return new Panel(mc, this)
                .setLayout(new HorizontalLayout().setHorizontalMargin(3).setSpacing(1))
                .addChild(connectorList)
                .addChild(listSlider)
                .setLayoutHint(new PositionalLayout.PositionalHint(2, 36, 169, 198));
    }

    private void hilightSelectedContainer(int index) {
        if (index < 0) {
            return;
        }
        ConnectedBlockClientInfo c = fromServer_connectedBlocks.get(index);
        if (c != null) {
            XNet.instance.clientInfo.hilightBlock(c.getPos().getPos(), System.currentTimeMillis() + 1000 * 5);
            Logging.message(mc.player, "The block is now highlighted");
            mc.player.closeScreen();
        }
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
        showingConnector = null;
        for (int j = 0 ; j < MAX_CHANNELS ; j++) {
            if (j != finalI) {
                channelButtons[j].setPressed(false);
                editingChannel = finalI;
            }
        }
    }

    private void removeConnector(SidedPos sidedPos) {
        sendServerCommand(XNetMessages.INSTANCE, TileEntityController.CMD_REMOVECONNECTOR,
                TypedMap.builder()
                        .put(PARAM_CHANNEL, getSelectedChannel())
                        .put(PARAM_POS, sidedPos.getPos())
                        .put(PARAM_SIDE, sidedPos.getSide().ordinal())
                        .build());
        refresh();
    }

    private void createConnector(SidedPos sidedPos) {
        sendServerCommand(XNetMessages.INSTANCE, TileEntityController.CMD_CREATECONNECTOR,
                TypedMap.builder()
                        .put(PARAM_CHANNEL, getSelectedChannel())
                        .put(PARAM_POS, sidedPos.getPos())
                        .put(PARAM_SIDE, sidedPos.getSide().ordinal())
                        .build());
        refresh();
    }

    private void removeChannel() {
        sendServerCommand(XNetMessages.INSTANCE, TileEntityController.CMD_REMOVECHANNEL,
                TypedMap.builder()
                        .put(PARAM_INDEX, getSelectedChannel())
                        .build());
        refresh();
    }

    private void createChannel(String typeId) {
        sendServerCommand(XNetMessages.INSTANCE, TileEntityController.CMD_CREATECHANNEL,
                TypedMap.builder()
                        .put(PARAM_INDEX, getSelectedChannel())
                        .put(PARAM_TYPE, typeId)
                        .build());
        refresh();
    }

    public void refresh() {
        fromServer_channels = null;
        fromServer_connectedBlocks = null;
        showingChannel = -1;
        showingConnector = null;
        needsRefresh = true;
        listDirty = 3;
        requestListsIfNeeded();
    }

    private void selectConnectorEditor(SidedPos sidedPos, int finalI) {
        editingConnector = sidedPos;
        selectChannelEditor(finalI);
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
                    ChannelEditorPanel editor = new ChannelEditorPanel(channelEditPanel, mc, this, editingChannel);
                    editor.label("Channel " + (editingChannel + 1))
                            .shift(5)
                            .toggle(TAG_ENABLED, "Enable processing on this channel", info.isEnabled())
                            .shift(5)
                            .text(TAG_NAME, "Channel name", info.getChannelName(), 65);
                    info.getChannelSettings().createGui(editor);

                    Button remove = new Button(mc, this).setText("x")
                            .setTextOffset(0, -1)
                            .setTooltips("Remove this channel")
                            .setLayoutHint(new PositionalLayout.PositionalHint(151, 1, 9, 10))
                            .addButtonEvent(parent -> removeChannel());
                    channelEditPanel.addChild(remove);
                    editor.setState(info.getChannelSettings());
                } else {
                    ChoiceLabel type = new ChoiceLabel(mc, this)
                            .setLayoutHint(new PositionalLayout.PositionalHint(5, 12, 95, 14));
                    for (IChannelType channelType : XNet.xNetApi.getChannels().values()) {
                        type.addChoices(channelType.getID());       // Show names?
                    }
                    Button create = new Button(mc, this)
                            .setText("Create")
                            .setLayoutHint(new PositionalLayout.PositionalHint(100, 12, 53, 14))
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

                    Button remove = new Button(mc, this).setText("x")
                            .setTextOffset(0, -1)
                            .setTooltips("Remove this connector")
                            .setLayoutHint(new PositionalLayout.PositionalHint(151, 1, 9, 10))
                            .addButtonEvent(parent -> removeConnector(editingConnector));

                    ConnectorEditorPanel editor = new ConnectorEditorPanel(connectorEditPanel, mc, this, editingChannel, editingConnector);

                    connectorInfo.getConnectorSettings().createGui(editor);
                    connectorEditPanel.addChild(remove);
                    editor.setState(connectorInfo.getConnectorSettings());
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

        connectorList.removeChildren();

        int sel = connectorList.getSelected();
        BlockPos prevPos = null;

        String selectedText = searchBar.getText().trim().toLowerCase();

        for (ConnectedBlockClientInfo connectedBlock : fromServer_connectedBlocks) {
            SidedPos sidedPos = connectedBlock.getPos();
            BlockPos coordinate = sidedPos.getPos();
            String name = connectedBlock.getName();
            String blockUnlocName = connectedBlock.getBlockUnlocName();
            String blockName = I18n.format(blockUnlocName).trim();

            int color = StyleConfig.colorTextInListNormal;

            Panel panel = new Panel(mc, this).setLayout(new HorizontalLayout().setHorizontalMargin(0).setSpacing(0));
            if (!selectedText.isEmpty()) {
                if (blockName.toLowerCase().contains(selectedText)) {
                    panel.setFilledBackground(0xffddeeaa);
                }
            }
            BlockRender br;
            if (coordinate.equals(prevPos)) {
                br = new BlockRender(mc, this);
            } else {
                br = new BlockRender(mc, this).setRenderItem(connectedBlock.getConnectedBlock());
                prevPos = coordinate;
            }
            br.setUserObject("block");
            panel.addChild(br);
            if (!name.isEmpty()) {
                br.setTooltips(TextFormatting.GREEN + "Connector: " + TextFormatting.WHITE + name,
                        TextFormatting.GREEN + "Block: " + TextFormatting.WHITE + blockName,
                        TextFormatting.GREEN + "Position: " + TextFormatting.WHITE + BlockPosTools.toString(coordinate),
                        TextFormatting.WHITE + "(doubleclick to highlight)");
            } else {
                br.setTooltips(TextFormatting.GREEN + "Block: " + TextFormatting.WHITE + blockName,
                        TextFormatting.GREEN + "Position: " + TextFormatting.WHITE + BlockPosTools.toString(coordinate),
                        TextFormatting.WHITE + "(doubleclick to highlight)");
            }

            panel.addChild(new Label(mc, this).setText(sidedPos.getSide().getName().substring(0, 1).toUpperCase()).setColor(color).setDesiredWidth(18));
            for (int i = 0 ; i < MAX_CHANNELS ; i++) {
                Button but = new Button(mc, this).setDesiredWidth(14);
                ChannelClientInfo info = fromServer_channels.get(i);
                if (info != null) {
                    ConnectorClientInfo clientInfo = findClientInfo(info, sidedPos);
                    if (clientInfo != null) {
                        IndicatorIcon icon = clientInfo.getConnectorSettings().getIndicatorIcon();
                        if (icon != null) {
                            but.setImage(icon.getImage(), icon.getU(), icon.getV(), icon.getIw(), icon.getIh());
                        }
                        String indicator = clientInfo.getConnectorSettings().getIndicator();
                        but.setText(indicator != null ? indicator : "");
                    }
                }
                int finalI = i;
                but.addButtonEvent(parent -> {
                    selectConnectorEditor(sidedPos, finalI);
                });
                panel.addChild(but);
            }
            connectorList.addChild(panel);
        }

        connectorList.setSelected(sel);
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
                    IndicatorIcon icon = info.getChannelSettings().getIndicatorIcon();
                    if (icon != null) {
                        channelButtons[i].setImage(icon.getImage(), icon.getU(), icon.getV(), icon.getIw(), icon.getIh());
                    }
                    String indicator = info.getChannelSettings().getIndicator();
                    if (indicator != null) {
                        channelButtons[i].setText(indicator + channel);
                    } else {
                        channelButtons[i].setText(channel);
                    }
                } else {
                    channelButtons[i].setImage(null, 0, 0, 0, 0);
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
        int currentRF = GenericEnergyStorageTileEntity.getCurrentRF();
        energyBar.setValue(currentRF);
        tileEntity.requestRfFromServer(XNet.MODID);
    }

    @Override
    protected void drawStackTooltips(int mouseX, int mouseY) {
        int x = Mouse.getEventX() * width / mc.displayWidth;
        int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        Widget<?> widget = window.getToplevel().getWidgetAtPosition(x, y);
        if (widget instanceof BlockRender) {
            if ("block".equals(widget.getUserObject())) {
                //System.out.println("GuiController.drawStackTooltips");
                return;     // Don't do the normal tooltip rendering
            }
        }
        super.drawStackTooltips(mouseX, mouseY);
    }
}
