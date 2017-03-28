package mcjty.xnet.blocks.cables;

import mcjty.xnet.blocks.generic.CableColor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class NetCableSetup {
    public static NetCableBlock netCableBlock;
    public static ConnectorBlock connectorBlock;
    public static AdvancedConnectorBlock advancedConnectorBlock;

    public static void init() {
        netCableBlock = new NetCableBlock();
        connectorBlock = new ConnectorBlock();
        advancedConnectorBlock = new AdvancedConnectorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initClient() {
        netCableBlock.initModel();
        connectorBlock.initModel();
        advancedConnectorBlock.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {
        netCableBlock.initItemModel();
        connectorBlock.initItemModel();
        advancedConnectorBlock.initItemModel();
    }

    public static void initCrafting() {
        GameRegistry.addRecipe(new ItemStack(netCableBlock, 16, CableColor.ROUTING.ordinal()), "srs", "rgr", "srs", 'r', Blocks.REDSTONE_BLOCK, 's', Items.STRING, 'g', Items.DIAMOND);
        GameRegistry.addRecipe(new ItemStack(netCableBlock, 16), "srs", "rgr", "srs", 'r', Items.REDSTONE, 's', Items.STRING, 'g', Items.GOLD_NUGGET);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(connectorBlock, 1), "lRl", "rgr", "lrl", 'r', Items.REDSTONE, 'l', "dyeBlue", 'g', Items.GOLD_INGOT, 'R', "chest"));
        GameRegistry.addRecipe(new ItemStack(connectorBlock, 1, CableColor.ROUTING.ordinal()), "rrr", "xMx", "rrr", 'M', new ItemStack(connectorBlock, 1), 'r', Items.REDSTONE, 'x', Items.GOLD_NUGGET);
        GameRegistry.addRecipe(new ItemStack(advancedConnectorBlock, 1), "ce", "dr", 'c', connectorBlock, 'e', Items.ENDER_PEARL, 'd', Items.DIAMOND, 'r', Items.REDSTONE);

        for (CableColor source : CableColor.VALUES) {
            if (source != CableColor.ROUTING) {
                for (CableColor dest : CableColor.VALUES) {
                    if (dest != source && dest != CableColor.ROUTING) {
                        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(netCableBlock, 1, dest.ordinal()), new ItemStack(netCableBlock, 1, source.ordinal()), dest.getDye()));
                        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(connectorBlock, 1, dest.ordinal()), new ItemStack(connectorBlock, 1, source.ordinal()), dest.getDye()));
                        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(advancedConnectorBlock, 1, dest.ordinal()), new ItemStack(advancedConnectorBlock, 1, source.ordinal()), dest.getDye()));
                    }
                }
            }
        }
    }
}
