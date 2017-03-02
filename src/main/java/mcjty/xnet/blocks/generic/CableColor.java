package mcjty.xnet.blocks.generic;

import net.minecraft.util.IStringSerializable;

public enum CableColor implements IStringSerializable {
    BLUE("blue"),
    RED("red"),
    YELLOW("yellow"),
    GREEN("green");

    public static final CableColor[] VALUES = CableColor.values();

    private final String name;

    CableColor(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
