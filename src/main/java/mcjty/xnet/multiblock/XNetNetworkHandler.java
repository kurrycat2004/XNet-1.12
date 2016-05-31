package mcjty.xnet.multiblock;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import elec332.core.nbt.NBTMap;
import mcjty.xnet.api.IXNetChannel;
import mcjty.xnet.handler.NetworkCallbacks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Elec332 on 10-5-2016.
 */
public class XNetNetworkHandler implements INBTSerializable<NBTTagCompound> {

    private XNetNetworkHandler(){
        channels = HashBiMap.create();
        channelSaveMap = NBTMap.newNBTMap(UUID.class, IXNetChannel.class);
    }

    private NBTMap<UUID, ResourceLocation> channelTypeSaveMap;
    private NBTMap<UUID, IXNetChannel> channelSaveMap;
    private final BiMap<UUID, IXNetChannel> channels;

    public void createNetwork(IXNetChannel.Factory factory){

    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    @Nonnull
    private ResourceLocation getResourceForType(IXNetChannel channel){
        Class<? extends IXNetChannel> clazz = channel.getClass();
        Map<Class, ResourceLocation> mappings = NetworkCallbacks.getClassMappings();
        ResourceLocation ret = mappings.get(clazz);
        if (ret == null){
            throw new IllegalStateException("XNetChannel of type: "+clazz.getName()+" has not been registered properly!");
        }
        return ret;
    }

}
