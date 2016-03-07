package mcjty.xnet.handler;

import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.registry.IWorldRegistry;
import mcjty.xnet.multiblock.XNetWorldGridRegistry;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 7-3-2016.
 */
public class WorldHandler implements IWorldRegistry {

    public static final AbstractWorldRegistryHolder<WorldHandler> instance;

    private WorldHandler(World world){
        //this.world = world;
        this.xNetWorldGridRegistry = new XNetWorldGridRegistry(world);
    }

    //private final World world;

    public final XNetWorldGridRegistry xNetWorldGridRegistry;

    /**
     * Gets called every tick
     */
    @Override
    public void tick() {
        xNetWorldGridRegistry.tick();
    }

    /**
     * Gets called when the world unloads, just before it is removed from the registry and made ready for the GC
     */
    @Override
    public void onWorldUnload() {
        xNetWorldGridRegistry.onWorldUnload();
    }






    ////////////////////////////////////////////////////

    /**
     * Dummy method to load the static initializer
     */
    public static void init(){
    }

    static {
        instance = new AbstractWorldRegistryHolder<WorldHandler>() {

            @Override
            public boolean serverOnly() {
                return true;
            }

            @Override
            public WorldHandler newRegistry(World world) {
                return new WorldHandler(world);
            }

        };
    }

}
