package mcjty.xnet.init;

import mcjty.xnet.blocks.cables.NetCableSetup;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes {

    public static void init(){
        NetCableSetup.initCrafting();

        Block machineFrame;
        if (Loader.isModLoaded("rftools")) {
            machineFrame = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("rftools", "machine_frame"));
        } else {
            machineFrame = Blocks.IRON_BLOCK;
        }

        GameRegistry.addRecipe(new ItemStack(ModBlocks.controllerBlock), "RCR", "rMr", "igi",
                'M', machineFrame, 'R', Items.REPEATER, 'C', Items.COMPARATOR, 'r', Items.REDSTONE,
                'i', Items.IRON_INGOT, 'g', Items.GOLD_INGOT);
    }

}
