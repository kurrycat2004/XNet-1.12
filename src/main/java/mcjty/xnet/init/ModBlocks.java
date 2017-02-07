package mcjty.xnet.init;

import mcjty.xnet.blocks.bakedmodel.CableModelBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static CableModelBlock cableBlock;

    public static void init() {
        cableBlock = new CableModelBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        cableBlock.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        cableBlock.initItemModel();
    }
}
