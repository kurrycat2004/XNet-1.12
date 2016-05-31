package mcjty.xnet.handler;

import com.google.common.collect.Maps;
import mcjty.xnet.XNet;
import mcjty.xnet.api.IXNetChannel;
import mcjty.xnet.varia.XNetResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import java.util.Map;

/**
 * Created by Elec332 on 10-5-2016.
 */
@SuppressWarnings("unchecked")
public enum NetworkCallbacks implements IForgeRegistry.AddCallback<IXNetChannel.Factory>, IForgeRegistry.ClearCallback<IXNetChannel.Factory>, IForgeRegistry.CreateCallback<IXNetChannel.Factory>  {

    INSTANCE;

    private static final ResourceLocation CLASS_MAPPING = new XNetResourceLocation("class_map");

    @Override
    public void onAdd(IXNetChannel.Factory obj, int id, Map<ResourceLocation, ?> slaveset) {
        Map<Class, ResourceLocation> map = (Map<Class, ResourceLocation>) slaveset.get(CLASS_MAPPING);
        ResourceLocation rl = XNet.networkRegistry.getKey(obj);
        map.put(obj.getTypeClass(), rl);
    }

    @Override
    public void onClear(Map<ResourceLocation, ?> slaveset) {
        ((Map)slaveset.get(CLASS_MAPPING)).clear();
    }

    @Override
    public void onCreate(Map<ResourceLocation, ?> slaveset) {
        ((Map<ResourceLocation, Object>)slaveset).put(CLASS_MAPPING, Maps.newHashMap());
    }

    public static Map<Class, ResourceLocation> getClassMappings(){
        return XNet.networkRegistry.getSlaveMap(CLASS_MAPPING, Map.class);
    }

}
