package mcjty.xnet.api.channels;

public interface IEditorGui {

    IEditorGui move(int x, int y);

    IEditorGui shift(int x);

    IEditorGui label(String txt);

    IEditorGui text(String tag, String value);

    IEditorGui toggle(String tag, boolean value);

    IEditorGui choices(String tag, String current, String... values);

    <T extends Enum<T>> IEditorGui choices(String tag, T current, T... values);

    IEditorGui redstoneMode(String tag, RSMode current);

    IEditorGui nl();
}
