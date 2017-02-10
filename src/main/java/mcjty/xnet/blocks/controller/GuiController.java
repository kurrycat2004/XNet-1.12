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
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.tools.MinecraftTools;
import mcjty.lib.varia.RedstoneMode;
import mcjty.xnet.XNet;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.network.PacketGetConsumers;
import mcjty.xnet.logic.SidedPos;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static mcjty.xnet.logic.ChannelInfo.MAX_CHANNELS;

public class GuiController extends GenericGuiContainer<TileEntityController> {

    public static final int SIDEWIDTH = 80;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 236;

    private WidgetList list;
    private int listDirty;

    private Panel channelEditPanel;
    private Panel consumerEditPanel;

    private ToggleButton channelButtons[] = new ToggleButton[MAX_CHANNELS];

    private SidedPos editing = null;
    private int editingChannel = -1;
    private EnergyBar energyBar;
    private ImageChoiceLabel redstoneMode;

    private static final ResourceLocation iconGuiElements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");
    private static final ResourceLocation mainBackground = new ResourceLocation(XNet.MODID, "textures/gui/controller.png");
    private static final ResourceLocation sideBackground = new ResourceLocation(XNet.MODID, "textures/gui/sidegui.png");

    // A copy of the consumers we're currently showing
    private List<SidedPos> consumers = null;

    // From server.
    public static List<SidedPos> fromServer_consumers = null;


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
        Panel listPanel = initConsumerListPanel();
        Panel channelSelectionPanel = initChannelSelectionPanel();
        initEditPanels();

        toplevel.addChild(channelSelectionPanel).addChild(listPanel).addChild(channelEditPanel).addChild(consumerEditPanel)
            .addChild(energyBar);

        window = new Window(this, toplevel);

        editing = null;
        editingChannel = -1;

        fromServer_consumers = null;
        listDirty = 0;
        XNetMessages.INSTANCE.sendToServer(new PacketGetConsumers(tileEntity.getPos()));
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
        consumerEditPanel = new Panel(mc, this).setLayout(new PositionalLayout())
                .setFilledRectThickness(-1)
                .setFilledBackground(StyleConfig.colorListBackground)
                .setLayoutHint(new PositionalLayout.PositionalHint(171, 60, 161, 52));
    }

    private Panel initConsumerListPanel() {
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
        for (int j = 0 ; j < MAX_CHANNELS ; j++) {
            if (j != finalI) {
                channelButtons[j].setPressed(false);
            }
        }

        channelEditPanel.removeChildren();
        if (channelButtons[finalI].isPressed()) {
            Widget label = new Label(mc, this).setText("Channel " + (finalI+1))
                    .setLayoutHint(new PositionalLayout.PositionalHint(4, 3, 60, 14));
            ChoiceLabel type = new ChoiceLabel(mc, this).addChoices("Item", "Energy", "Fluid")
                    .setLayoutHint(new PositionalLayout.PositionalHint(66, 3, 60, 14));
            ToggleButton enabled = new ToggleButton(mc, this).setCheckMarker(true).setPressed(true)
                    .setLayoutHint(new PositionalLayout.PositionalHint(130, 3, 13, 14));
            ChoiceLabel mode = new ChoiceLabel(mc, this).addChoices("Round Robin", "Random", "First")
                    .setLayoutHint(new PositionalLayout.PositionalHint(4, 20, 100, 14));

            channelEditPanel.addChild(label).addChild(enabled).addChild(type).addChild(mode);
        }
    }

    private void selectConsumerEditor(SidedPos sidedPos, ToggleButton but, int finalI) {
        consumers = null;
        if (but.isPressed()) {
            editing = sidedPos;
            editingChannel = finalI;
        } else {
            editing = null;
            editingChannel = -1;
        }
        consumerEditPanel.removeChildren();
        if (but.isPressed()) {
            if (editingChannel != getSelectedChannel()) {
                selectChannelEditor(editingChannel);
            }

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

            consumerEditPanel.addChild(type).addChild(redstoneMode).addChild(label1).addChild(oreDict).addChild(meta)
                .addChild(label2).addChild(speed);
        }

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

    private int getSelectedChannel() {
        for (int i = 0 ; i < MAX_CHANNELS ; i++) {
            if (channelButtons[i].isPressed()) {
                return i;
            }
        }
        return -1;
    }

    private void populateList() {
        List<SidedPos> newConsumers = fromServer_consumers;
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
        BlockPos prevPos = null;
        for (SidedPos sidedPos : consumers) {
            BlockPos coordinate = sidedPos.getPos();
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
            if (coordinate.equals(prevPos)) {
                panel.addChild(new BlockRender(mc, this));
            } else {
                panel.addChild(new BlockRender(mc, this).setRenderItem(block));
                prevPos = coordinate;
            }
//            panel.addChild(new Label(mc, this).setText(displayName).setColor(color).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT).setDesiredWidth(90));
            panel.addChild(new Label(mc, this).setText(sidedPos.getSide().getName().substring(0, 1).toUpperCase()).setColor(color).setDesiredWidth(18));
            for (int i = 0 ; i < MAX_CHANNELS ; i++) {
                ToggleButton but = new ToggleButton(mc, this).setDesiredWidth(14);
                if (i == 3) {
                    if (index == 3) {
                        but.setText("I");
                    } else if (index == 4) {
                        but.setText("E");
                    } else if (index == 0) {
                        but.setText("E");
                    }
                }
                but.setPressed(editingChannel == i && sidedPos.equals(editing));
                int finalI = i;
                but.addButtonEvent(parent -> {
                    selectConsumerEditor(sidedPos, but, finalI);
                });
                panel.addChild(but);
            }
            list.addChild(panel);

            index++;
        }

        list.setSelected(sel);
    }

    private int getRelativeX() {
        return Mouse.getEventX() * width / mc.displayWidth;
    }

    private int getRelativeY() {
        return height - Mouse.getEventY() * height / mc.displayHeight - 1;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        requestListsIfNeeded();
        populateList();
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
