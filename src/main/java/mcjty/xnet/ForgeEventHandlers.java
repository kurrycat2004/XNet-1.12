package mcjty.xnet;

import java.util.HashMap;
import java.util.Map;

import mcjty.lib.McJtyRegister;
import mcjty.lib.datafix.fixes.TileEntityNamespace;
import mcjty.xnet.multiblock.XNetWirelessChannels;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ForgeEventHandlers {

    private static final int AMOUNT = 10;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ModFixs modFixs = FMLCommonHandler.instance().getDataFixer().init(XNet.MODID, 1);
        McJtyRegister.registerBlocks(XNet.instance, event.getRegistry(), modFixs, 1);

        // We used to accidentally register TEs with names like "minecraft:xnet_facade" instead of "xnet:facade".
        // Set up a DataFixer to map these incorrect names to the correct ones, so that we don't break old saved games.
        // @todo Remove all this if we ever break saved-game compatibility.
        Map<String, String> oldToNewIdMap = new HashMap<>();
        oldToNewIdMap.put(XNet.MODID + "_facade", XNet.MODID + ":facade");
        oldToNewIdMap.put("minecraft:" + XNet.MODID + "_facade", XNet.MODID + ":facade");
        oldToNewIdMap.put(XNet.MODID + "_connector", XNet.MODID + ":connector");
        oldToNewIdMap.put("minecraft:" + XNet.MODID + "_connector", XNet.MODID + ":connector");
        oldToNewIdMap.put(XNet.MODID + "_advanced_connector", XNet.MODID + ":advanced_connector");
        oldToNewIdMap.put("minecraft:" + XNet.MODID + "_advanced_connector", XNet.MODID + ":advanced_connector");
        modFixs.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityNamespace(oldToNewIdMap, 1));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        McJtyRegister.registerItems(XNet.instance, event.getRegistry());
    }

    private int cnt = AMOUNT;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER) {
            cnt--;
            if (cnt > 0) {
                return;
            }
            cnt = AMOUNT;

            XNetWirelessChannels data = XNetWirelessChannels.getWirelessChannels(event.world);
            data.tick(event.world, AMOUNT);
        }
    }
}
