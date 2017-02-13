package mcjty.xnet.api.channels;

public interface IEditorGui {

    IEditorGui move(int x, int y);

    IEditorGui shift(int x);

    IEditorGui label(String txt);

    IEditorGui text(String value);

    IEditorGui toggle(boolean value);

    IEditorGui choices(String current, String... values);

    <T extends Enum<T>> IEditorGui choices(T current, T... values);

    IEditorGui redstoneMode(Object current);    // @todo type

    IEditorGui nl();
}
