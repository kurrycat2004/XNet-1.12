package mcjty.xnet.init;

import mcjty.xnet.blocks.cables.NetCableSetup;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModRecipes {

    public static void init(){
        NetCableSetup.initCrafting();

        Block machineFrame;
        if (Loader.isModLoaded("rftools")) {
            machineFrame = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftools", "machine_frame"));
        } else {
            machineFrame = Blocks.IRON_BLOCK;
        }

    }

}
