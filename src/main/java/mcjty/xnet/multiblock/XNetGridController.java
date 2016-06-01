package mcjty.xnet.multiblock;

import com.google.common.collect.Maps;
import elec332.core.util.NBT;
import mcjty.xnet.XNet;
import mcjty.xnet.api.IXNetChannel;
import mcjty.xnet.api.IXNetController;
import mcjty.xnet.blocks.controller.TileEntityController;
import mcjty.xnet.handler.NetworkCallbacks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.*;

/**
 * Created by Elec332 on 31-5-2016.
 */
public class XNetGridController implements IXNetController {

    public XNetGridController(TileEntityController host){
        this.channels = Maps.newHashMap();
        this.channels_ = Collections.unmodifiableMap(channels);
        this.host = host;
    }

    private TileEntityController host;
    private final Map<UUID, IXNetChannel<?, ?>> channels;
    private final Map<UUID, IXNetChannel<?, ?>> channels_;

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound save = new NBTTagCompound();
        NBTTagList channelList = new NBTTagList();
        for (Map.Entry<UUID, IXNetChannel<?, ?>> channel : channels.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("data", channel.getValue().serializeNBT());
            tag.setString("id", channel.getKey().toString());
            tag.setString("type", NetworkCallbacks.getClassMappings().get(channel.getClass()).toString());
            channelList.appendTag(tag);
        }
        save.setTag("channels", channelList);
        return save;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserializeNBT(NBTTagCompound nbt) {
        channels.clear();
        NBTTagList tagList = nbt.getTagList("channels", NBT.NBTData.COMPOUND.getID());
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            ResourceLocation name = new ResourceLocation(tag.getString("type"));
            if (XNet.networkRegistry.containsKey(name)) {
                IXNetChannel channel = XNet.networkRegistry.getObject(name).createNewChannel(this);
                channel.deserializeNBT(tag.getTag("data"));
                channels.put(UUID.fromString(tag.getString("id")), channel);
            }
        }
    }

    @Override
    public void addChannel(String name, IXNetChannel.Factory factory) {

    }

    @Override
    public void removeChannel(UUID uuid) {

    }

    @Override
    public void markDirty() {
        host.markDirty();
    }

    @Override
    public void removeController() {
        host.deactivate();
    }

    @Override
    public Collection<IXNetChannel<?, ?>> getChannels() {
        return channels_.values();
    }

    public void unload(){

    }

}
