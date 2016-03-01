package mcjty.xnet;

import mcjty.xnet.blocks.DummyBlock;
import mcjty.xnet.blocks.NetCableSetup;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static DummyBlock dummyBlock;

    public static void init() {
        dummyBlock = new DummyBlock();

        NetCableSetup.init();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        dummyBlock.initModel();

        NetCableSetup.initClient();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        NetCableSetup.initItemModels();
    }

    public static void initCrafting() {
        NetCableSetup.initCrafting();
    }
}
