package mcjty.xnet;


import elec332.core.client.model.RenderingRegistry;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.xnet.client.XNetClientModelLoader;
import mcjty.xnet.init.ModBlocks;
import mcjty.xnet.init.ModItems;
import mcjty.xnet.init.ModRecipes;
import mcjty.xnet.multiblock.CableNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = XNet.MODID, name=XNet.MODNAME, dependencies =
        "required-after:Forge@["+XNet.MIN_FORGE_VER+
                ",);required-after:McJtyLib@["+XNet.MIN_MCJTYLIB_VER+",)",
        version = XNet.MODVERSION)
public class XNet implements ModBase {

    public static final String MODID = "xnet";
    public static final String MODNAME = "XNet";
    public static final String MODVERSION = "0.1.0";

    public static final String MIN_FORGE_VER = "11.15.1.1722";
    public static final String MIN_MCJTYLIB_VER = "1.8.9-1.8.1beta7";

    @SidedProxy
    public static CommonProxy proxy;

    @Mod.Instance(value = MODID)
    public static XNet instance;

    public static Logger logger;

    public static CreativeTabs tabXNet = new CreativeTabs("XNet") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.anvil);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        CableNetwork.clearInstance();
    }


    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent e) {
            // Initialize our packet handler. Make sure the name is
            // 20 characters or less!
//            PacketHandler.registerMessages("xnet");
            MainCompatHandler.registerWaila();

            // Initialization of blocks and items typically goes here:
            ModBlocks.init();
            ModItems.init();
            ModRecipes.init();
        }

        public void init(FMLInitializationEvent e) {

        }

        public void postInit(FMLPostInitializationEvent e) {

        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientProxy extends CommonProxy {

        private static XNetClientModelLoader modelLoader;

        @Override
        public void preInit(FMLPreInitializationEvent e) {
            super.preInit(e);
            modelLoader = new XNetClientModelLoader();
            modelLoader.setModelLocations();
            RenderingRegistry.instance().registerLoader(modelLoader);
            MinecraftForge.EVENT_BUS.register(modelLoader);
        }

    }

    public static class ServerProxy extends CommonProxy {
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer entityPlayer, int i, String s) {
        // @todo
    }

}
