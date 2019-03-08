package mcjty.xnet.proxy;

import mcjty.lib.McJtyLib;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import mcjty.lib.varia.WrenchChecker;
import mcjty.xnet.CommandHandler;
import mcjty.xnet.ForgeEventHandlers;
import mcjty.xnet.XNet;
import mcjty.xnet.apiimpl.energy.EnergyChannelType;
import mcjty.xnet.apiimpl.fluids.FluidChannelType;
import mcjty.xnet.apiimpl.items.ItemChannelType;
import mcjty.xnet.apiimpl.logic.LogicChannelType;
import mcjty.xnet.config.GeneralConfiguration;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.init.ModBlocks;
import mcjty.xnet.init.ModItems;
import mcjty.xnet.network.XNetMessages;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Level;

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    public static boolean rftools = false;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        McJtyLib.registerMod(XNet.instance);    // @todo why only xnet?

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        CommandHandler.registerCommands();

        GeneralConfig.preInit(e);

        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "xnet", "xnet.cfg"));

        readMainConfig();

        XNetMessages.registerMessages("xnet");

        ModItems.init();
        ModBlocks.init();

        XNet.xNetApi.registerConsumerProvider((world, iWorldBlob, network1) -> iWorldBlob.getConsumers(network1));
        XNet.xNetApi.registerChannelType(new ItemChannelType());
        XNet.xNetApi.registerChannelType(new EnergyChannelType());
        XNet.xNetApi.registerChannelType(new FluidChannelType());
        XNet.xNetApi.registerChannelType(new LogicChannelType());

        rftools = Loader.isModLoaded("rftools");

        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }

    @Override
    public void createTabs() {
        createTab("XNet", new ItemStack(Item.getItemFromBlock(Blocks.ANVIL)));
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(GeneralConfiguration.CATEGORY_GENERAL, "General settings");

            GeneralConfiguration.init(cfg);
        } catch (Exception e1) {
            FMLLog.log(Level.ERROR, e1, "Problem loading config file!");
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(XNet.instance, new GuiProxy());
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        mainConfig = null;
        WrenchChecker.init();
    }
}
