package mcjty.xnet.api.channels;

public interface IEditorGui {

    IEditorGui label(String txt);

    IEditorGui text(String value);

    IEditorGui toggle(boolean value);

    IEditorGui choices(String current, String... values);

    <T extends Enum<T>> IEditorGui choices(Enum<T> current, T... values);

    IEditorGui redstoneMode(Object current);    // @todo type

    IEditorGui nl();
}
