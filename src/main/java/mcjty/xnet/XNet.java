package mcjty.xnet;


import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.xnet.api.IXNet;
import mcjty.xnet.apiimpl.XNetApi;
import mcjty.xnet.commands.CommandCheck;
import mcjty.xnet.commands.CommandDump;
import mcjty.xnet.commands.CommandRebuild;
import mcjty.xnet.compat.TOPSupport;
import mcjty.xnet.compat.WAILASupport;
import mcjty.xnet.items.manual.GuiXNetManual;
import mcjty.xnet.multiblock.XNetBlobData;
import mcjty.xnet.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Function;

@Mod(modid = XNet.MODID, name = XNet.MODNAME,
        dependencies =
                        "required-after:mcjtylib_ng@[" + XNet.MIN_MCJTYLIB_VER + ",);" +
                        "after:rftools@[" + XNet.MIN_RFTOOLS_VER + ",);" +
                        "after:forge@[" + XNet.MIN_FORGE11_VER + ",)",
        acceptedMinecraftVersions = "[1.12,1.13)",
        version = XNet.MODVERSION)
public class XNet implements ModBase {

    public static final String MODID = "xnet";
    public static final String MODNAME = "XNet";
    public static final String MODVERSION = "1.6.9";

    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_MCJTYLIB_VER = "2.6.2";
    public static final String MIN_RFTOOLS_VER = "7.23";

    public static final String SHIFT_MESSAGE = "<Press Shift>";

    @SidedProxy(clientSide = "mcjty.xnet.proxy.ClientProxy", serverSide = "mcjty.xnet.proxy.ServerProxy")
    public static CommonProxy proxy;

    public ClientInfo clientInfo = new ClientInfo();

    @Mod.Instance(MODID)
    public static XNet instance;
    public static Logger logger;

    public static boolean rftools = false;
    public static boolean redstoneflux = false;
    public static XNetApi xNetApi = new XNetApi();

    public static CreativeTabs tabXNet = new CreativeTabs("XNet") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(Blocks.ANVIL));
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        McJtyLib.registerMod(this);

        logger = event.getModLog();
        rftools = Loader.isModLoaded("rftools");
        redstoneflux = Loader.isModLoaded("redstoneflux");

        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();

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
//        event.registerServerCommand(new CommandGen());
        event.registerServerCommand(new CommandRebuild());
        event.registerServerCommand(new CommandCheck());
    }

    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void openManual(EntityPlayer player, int bookIndex, String page) {
        GuiXNetManual.locatePage = page;
        player.openGui(XNet.instance, bookIndex, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("getXNet")) {
                Optional<Function<IXNet, Void>> value = message.getFunctionValue(IXNet.class, Void.class);
                if (value.isPresent()) {
                    value.get().apply(xNetApi);
                } else {
                    logger.warn("Some mod didn't return a valid result with getXNet!");
                }
            }
        }
    }

    @Override
    public void handleTopExtras() {
        TOPSupport.registerTopExtras();
    }

    @Override
    public void handleWailaExtras() {
        WAILASupport.registerWailaExtras();
    }
}
