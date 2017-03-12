package mcjty.xnet.init;

import mcjty.xnet.items.manual.XNetManualItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    public static XNetManualItem xNetManualItem;

    public static void init() {
        xNetManualItem = new XNetManualItem();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        xNetManualItem.initModel();
    }
}
