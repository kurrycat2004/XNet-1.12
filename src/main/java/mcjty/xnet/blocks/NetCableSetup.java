package mcjty.xnet.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetCableSetup {
    public static NetCableBlock netCableBlock;
    public static EnergyConnectorBlock energyConnectorBlock;
    public static ItemConnectorBlock itemConnectorBlock;

    public static void init() {
        netCableBlock = new NetCableBlock();
        energyConnectorBlock = new EnergyConnectorBlock();
        itemConnectorBlock = new ItemConnectorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        netCableBlock.initModel();
        energyConnectorBlock.initModel();
        itemConnectorBlock.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        netCableBlock.initItemModel();
        energyConnectorBlock.initItemModel();
        itemConnectorBlock.initItemModel();
    }

    public static void initCrafting() {
        ItemStack lapisStack = new ItemStack(Items.dye, 1, 4);
        GameRegistry.addRecipe(new ItemStack(netCableBlock, 8), "srs", "rgr", "srs", 'r', Items.redstone, 's', Items.string, 'g', Items.gold_ingot);
        GameRegistry.addRecipe(new ItemStack(energyConnectorBlock, 1), "lRl", "rgr", "lrl", 'r', Items.redstone, 'l', lapisStack, 'g', Items.gold_ingot, 'R', Blocks.redstone_block);
        GameRegistry.addRecipe(new ItemStack(itemConnectorBlock, 1), "lRl", "rgr", "lrl", 'r', Items.redstone, 'l', lapisStack, 'g', Items.gold_ingot, 'R', Blocks.chest);
    }
}
