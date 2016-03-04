package mcjty.xnet.varia;

import mcjty.xnet.XNet;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 4-3-2016.
 */
public class XNetResourceLocation extends ResourceLocation {

    public XNetResourceLocation(String resourcePath) {
        super(XNet.MODID, resourcePath);
    }

}
