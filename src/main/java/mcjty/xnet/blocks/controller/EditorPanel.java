package mcjty.xnet.blocks.controller;

import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.varia.RedstoneMode;
import mcjty.xnet.api.channels.IEditorGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.StringUtils;

public class EditorPanel implements IEditorGui {

    private final Panel panel;
    private final Minecraft mc;
    private final Gui gui;

    private int x;
    private int y;

    public EditorPanel(Panel panel, Minecraft mc, Gui gui) {
        this.panel = panel;
        this.mc = mc;
        this.gui = gui;
        x = 4;
        y = 3;
    }

    @Override
    public IEditorGui move(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public IEditorGui shift(int x) {
        this.x += x;
        return this;
    }

    private void fitWidth(int w) {
        if (x + w > panel.getBounds().width) {
            nl();
        }
    }

    @Override
    public IEditorGui label(String txt) {
        int w = mc.fontRenderer.getStringWidth(txt);
        fitWidth(w);
        Label label = new Label(mc, gui).setText(txt);
        label.setLayoutHint(new PositionalLayout.PositionalHint(x, y, w, 14));
        panel.addChild(label);
        x += w;
        return this;
    }

    @Override
    public IEditorGui text(String value) {
        int w = 60;
        fitWidth(w);
        TextField text = new TextField(mc, gui).setText(value)
                .setLayoutHint(new PositionalLayout.PositionalHint(x, y, w, 14));
        panel.addChild(text);
        x += w;
        return this;
    }

    @Override
    public IEditorGui toggle(boolean value) {
        int w = 20;
        fitWidth(20);
        ToggleButton toggle = new ToggleButton(mc, gui).setCheckMarker(true).setPressed(value)
                .setLayoutHint(new PositionalLayout.PositionalHint(x, y, w, 14));
        panel.addChild(toggle);
        x += w;
        return this;
    }

    @Override
    public IEditorGui choices(String current, String... values) {
        int w = 10;
        for (String s : values) {
            w = Math.max(w, mc.fontRenderer.getStringWidth(s) + 10);
        }
        fitWidth(w);
        ChoiceLabel choice = new ChoiceLabel(mc, gui).addChoices(values).setChoice(current)
                .setLayoutHint(new PositionalLayout.PositionalHint(x, y, w, 14));
        panel.addChild(choice);
        x += w;
        return this;
    }

    @Override
    public <T extends Enum<T>> IEditorGui choices(T current, T... values) {
        String[] strings = new String[values.length];
        int i = 0;
        for (T s : values) {
            strings[i++] = StringUtils.capitalize(s.name().toLowerCase());
        }
        return choices(StringUtils.capitalize(current.name().toLowerCase()), strings);
    }

    @Override
    public IEditorGui redstoneMode(Object current) {
        int w = 16;
        fitWidth(w);
        ImageChoiceLabel redstoneMode = new ImageChoiceLabel(mc, gui)
                .addChoice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", GuiController.iconGuiElements, 0, 0)
                .addChoice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", GuiController.iconGuiElements, 16, 0)
                .addChoice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", GuiController.iconGuiElements, 32, 0);
        redstoneMode.setLayoutHint(new PositionalLayout.PositionalHint(x, y-1, w, 16));
        panel.addChild(redstoneMode);
        x += w;
        return this;
    }

    @Override
    public IEditorGui nl() {
        y += 16;
        x = 4;
        return this;
    }
}
