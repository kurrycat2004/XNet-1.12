package mcjty.xnet.api.gui;

import mcjty.xnet.api.channels.RSMode;

public interface IEditorGui {

    IEditorGui move(int x, int y);

    IEditorGui shift(int x);

    IEditorGui label(String txt);

    IEditorGui text(String tag, String tooltip, String value);

    IEditorGui integer(String tag, String tooltip, Integer value);

    IEditorGui real(String tag, String tooltip, Double value);

    IEditorGui toggle(String tag, String tooltip, boolean value);

    IEditorGui choices(String tag, String tooltip, String current, String... values);

    <T extends Enum<T>> IEditorGui choices(String tag, String tooltip, T current, T... values);

    IEditorGui redstoneMode(String tag, RSMode current);

    IEditorGui nl();
}
