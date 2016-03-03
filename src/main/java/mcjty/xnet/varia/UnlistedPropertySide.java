package mcjty.xnet.varia;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertySide implements IUnlistedProperty<EnumFacing> {

    public UnlistedPropertySide(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(EnumFacing value) {
        return true;
    }

    @Override
    public Class<EnumFacing> getType() {
        return EnumFacing.class;
    }

    @Override
    public String valueToString(EnumFacing value) {
        return value.toString();
    }

}
