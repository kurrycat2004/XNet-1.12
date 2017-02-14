package mcjty.xnet.blocks.controller;

import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.network.Argument;
import mcjty.xnet.logic.SidedPos;
import net.minecraft.client.Minecraft;

public class ChannelEditorPanel extends AbstractEditorPanel {

    private final int channel;

    @Override
    protected void update(String tag, Object value) {
        data.put(tag, value);
        Argument[] args = new Argument[data.size() + 3];
        int i = 0;
        args[i++] = new Argument("channel", channel);
        performUpdate(args, i, TileEntityController.CMD_UPDATECHANNEL);
    }

    public ChannelEditorPanel(Panel panel, Minecraft mc, GuiController gui, int channel) {
        super(panel, mc, gui);
        this.channel = channel;
        nl();
    }
}
