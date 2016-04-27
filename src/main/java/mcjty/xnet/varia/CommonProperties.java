package mcjty.xnet.varia;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by Elec332 on 27-4-2016.
 */
public class CommonProperties {

    public static final IUnlistedProperty<EnumFacing> FACING_PROPERTY = new UniversalUnlistedProperty<EnumFacing>("facing", EnumFacing.class);

}
