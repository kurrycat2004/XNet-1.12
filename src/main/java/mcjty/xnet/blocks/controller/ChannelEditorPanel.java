package mcjty.xnet.blocks.controller;

import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.network.Argument;
import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.logic.SidedPos;
import net.minecraft.client.Minecraft;

import java.util.Map;

public class ChannelEditorPanel extends AbstractEditorPanel {

    private final int channel;

    @Override
    protected void update(String tag, Object value) {
        data.put(tag, value);
        Argument[] args = new Argument[data.size() + 1];
        int i = 0;
        args[i++] = new Argument("channel", channel);
        performUpdate(args, i, TileEntityController.CMD_UPDATECHANNEL);
    }

    public ChannelEditorPanel(Panel panel, Minecraft mc, GuiController gui, int channel) {
        super(panel, mc, gui);
        this.channel = channel;
    }

    public void setState(IChannelSettings settings) {
        for (Map.Entry<String, Widget> entry : components.entrySet()) {
            entry.getValue().setEnabled(settings.isEnabled(entry.getKey()));
        }
    }
}
