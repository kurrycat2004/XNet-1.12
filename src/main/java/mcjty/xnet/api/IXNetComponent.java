package mcjty.xnet.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Elec332 on 1-3-2016.
 */
public abstract class IXNetComponent implements INBTSerializable<NBTTagCompound> {

    public IXNetComponent(){
        this.connections = Maps.newHashMap();
        this.connections_ = Collections.unmodifiableMap(connections);
    }

    private final Map<UUID, IXNetChannel> connections, connections_;

    public final void addConnection(@Nonnull IXNetChannel<?, ?> channel, @Nonnull UUID id){
        connections.put(id, channel);
        onConnectedToChannel(channel, id);
    }

    public final void removeConnection(@Nullable IXNetChannel<?, ?> channel, @Nonnull UUID id){
        connections.remove(id);
        onDisconnectedFromChannel(channel, id);
    }

    public final void nullifyChannels(){
        for (UUID uuid : Sets.newHashSet(connections.keySet())){
            connections.put(uuid, null);
        }
    }

    @Nonnull
    public final Set<UUID> getConnections(){
        return connections_.keySet();
    }

    protected void onConnectedToChannel(@Nonnull IXNetChannel<?, ?> channel, @Nonnull UUID id){
    }

    protected void onDisconnectedFromChannel(@Nullable IXNetChannel<?, ?> channel, @Nonnull UUID id){
    }

    @Override
    @Nonnull
    public NBTTagCompound serializeNBT() {
        NBTTagCompound save = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (UUID uuid : connections.keySet()){
            list.appendTag(new NBTTagString(uuid.toString()));
        }
        save.setTag("connections", list);
        return save;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        connections.clear();
        NBTTagList list = nbt.getTagList("connections", 8);
        for (int i = 0; i < list.tagCount(); i++) {
            connections.put(UUID.fromString(list.getStringTagAt(i)), null);
        }
    }

}
