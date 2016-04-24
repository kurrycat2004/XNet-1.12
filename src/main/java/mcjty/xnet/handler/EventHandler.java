package mcjty.xnet.handler;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Elec332 on 7-3-2016.
 */
public class EventHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){
        WorldHandler.instance.get(event.getWorld()); //Initialize handler for the specific world
    }

}
