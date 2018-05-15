package mcjty.xnet.proxy;

import com.google.common.util.concurrent.ListenableFuture;
import mcjty.lib.McJtyLibClient;
import mcjty.lib.font.TrueTypeFont;
import mcjty.xnet.RenderWorldLastEventHandler;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.generic.BakedModelLoader;
import mcjty.xnet.init.ModBlocks;
import mcjty.xnet.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Callable;

public class ClientProxy extends CommonProxy {

    public static TrueTypeFont font;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(this);
        OBJLoader.INSTANCE.addDomain(XNet.MODID);
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        McJtyLibClient.preInit(e);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModItems.initModels();
        ModBlocks.initModels();
    }

    @SubscribeEvent
    public void colorHandlerEventBlock(ColorHandlerEvent.Block event) {
        ModBlocks.initColorHandlers(event.getBlockColors());
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ModBlocks.initItemModels();
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt) {
        RenderWorldLastEventHandler.tick(evt);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public <V> ListenableFuture<V> addScheduledTaskClient(Callable<V> callableToSchedule) {
        return Minecraft.getMinecraft().addScheduledTask(callableToSchedule);
    }

    @Override
    public ListenableFuture<Object> addScheduledTaskClient(Runnable runnableToSchedule) {
        return Minecraft.getMinecraft().addScheduledTask(runnableToSchedule);
    }
}
