package mcjty.xnet.blocks.cables;

import mcjty.lib.MyGameReg;
import mcjty.xnet.blocks.generic.CableColor;
import mcjty.xnet.init.ModItems;
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
        MyGameReg.addRecipe(new ItemStack(netCableBlock, 32, CableColor.ROUTING.ordinal()), "srs", "rgr", "srs", 'r', Blocks.REDSTONE_BLOCK, 's', Items.STRING, 'g', Items.GOLD_INGOT);
        MyGameReg.addRecipe(new ItemStack(netCableBlock, 16), "srs", "rgr", "srs", 'r', Items.REDSTONE, 's', Items.STRING, 'g', Items.GOLD_NUGGET);
        MyGameReg.addRecipe(new ItemStack(connectorBlock, 1), "lRl", "rgr", "lrl", 'r', Items.REDSTONE, 'l', "dyeBlue", 'g', Items.GOLD_INGOT, 'R', "chest");
        MyGameReg.addRecipe(new ItemStack(connectorBlock, 1, CableColor.ROUTING.ordinal()), "rrr", "xMx", "rrr", 'M', new ItemStack(connectorBlock, 1), 'r', Items.REDSTONE, 'x', Items.GOLD_NUGGET);
        MyGameReg.addRecipe(new ItemStack(advancedConnectorBlock, 1), "ce", "dr", 'c', connectorBlock, 'e', Items.ENDER_PEARL, 'd', Items.DIAMOND, 'r', Items.REDSTONE);
        MyGameReg.addRecipe(new ItemStack(ModItems.upgradeItem, 1), "ce", "dr", 'c', Items.PAPER, 'e', Items.ENDER_PEARL, 'd', Items.DIAMOND, 'r', Items.REDSTONE);

        // @todo recipes
//        for (CableColor source : CableColor.VALUES) {
//            if (source != CableColor.ROUTING) {
//                for (CableColor dest : CableColor.VALUES) {
//                    if (dest != source && dest != CableColor.ROUTING) {
//                        MyGameReg.addRecipe(new ItemStack(netCableBlock, 1, dest.ordinal()), new ItemStack(netCableBlock, 1, source.ordinal()), dest.getDye());
//                        MyGameReg.addRecipe(new ItemStack(connectorBlock, 1, dest.ordinal()), new ItemStack(connectorBlock, 1, source.ordinal()), dest.getDye());
//                        MyGameReg.addRecipe(new ItemStack(advancedConnectorBlock, 1, dest.ordinal()), new ItemStack(advancedConnectorBlock, 1, source.ordinal()), dest.getDye());
//                    }
//                }
//            }
//        }
    }
}
