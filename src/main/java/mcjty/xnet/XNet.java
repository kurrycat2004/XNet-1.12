package mcjty.xnet;


import mcjty.lib.base.ModBase;
import mcjty.lib.compat.CompatCreativeTabs;
import mcjty.xnet.commands.CommandDump;
import mcjty.xnet.multiblock.XNetBlobData;
import mcjty.xnet.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

@Mod(modid = XNet.MODID, name = XNet.MODNAME,
        dependencies =
                        "required-after:mcjtylib_ng@[" + XNet.MIN_MCJTYLIB_VER + ",);" +
                        "required-after:compatlayer@[" + XNet.COMPATLAYER_VER + ",);" +
                        "after:Forge@[" + XNet.MIN_FORGE10_VER + ",);" +
                        "after:forge@[" + XNet.MIN_FORGE11_VER + ",)",
        version = XNet.MODVERSION,
        acceptedMinecraftVersions = "[1.10,1.12)")
public class XNet implements ModBase {

    public static final String MODID = "xnet";
    public static final String MODNAME = "XNet";
    public static final String MODVERSION = "0.1.0";

    public static final String MIN_FORGE10_VER = "12.18.2.2116";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "2.3.5";
    public static final String COMPATLAYER_VER = "0.1.7";

    @SidedProxy(clientSide = "mcjty.xnet.proxy.ClientProxy", serverSide = "mcjty.xnet.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static XNet instance;
    public static Logger logger;

    public static CreativeTabs tabXNet = new CompatCreativeTabs("XNet") {
        @Override
        protected Item getItem() {
            return Item.getItemFromBlock(Blocks.ANVIL);
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
        XNetBlobData.clearInstance();
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDump());
    }

    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer entityPlayer, int i, String s) {
        // @todo
    }

}
