package mcjty.xnet.proxy;

import mcjty.lib.base.GeneralConfig;
import mcjty.lib.network.PacketHandler;
import mcjty.lib.proxy.AbstractCommonProxy;
import mcjty.lib.varia.WrenchChecker;
import mcjty.xnet.CommandHandler;
import mcjty.xnet.ForgeEventHandlers;
import mcjty.xnet.XNet;
import mcjty.xnet.api.net.IWorldBlob;
import mcjty.xnet.apiimpl.energy.EnergyChannelType;
import mcjty.xnet.apiimpl.fluids.FluidChannelType;
import mcjty.xnet.apiimpl.items.ItemChannelType;
import mcjty.xnet.apiimpl.logic.LogicChannelType;
import mcjty.xnet.config.GeneralConfiguration;
import mcjty.xnet.gui.GuiProxy;
import mcjty.xnet.init.ModBlocks;
import mcjty.xnet.init.ModItems;
import mcjty.xnet.network.XNetMessages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;

import java.io.File;

public abstract class CommonProxy extends AbstractCommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        CommandHandler.registerCommands();

        GeneralConfig.preInit(e);

        mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "xnet", "xnet.cfg"));

        readMainConfig();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(XNet.MODID, "xnet");
        XNetMessages.registerNetworkMessages(network);

        ModItems.init();
        ModBlocks.init();

        XNet.xNetApi.registerConsumerProvider(IWorldBlob::getConsumers);
        XNet.xNetApi.registerChannelType(new ItemChannelType());
        XNet.xNetApi.registerChannelType(new EnergyChannelType());
        XNet.xNetApi.registerChannelType(new FluidChannelType());
        XNet.xNetApi.registerChannelType(new LogicChannelType());
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
