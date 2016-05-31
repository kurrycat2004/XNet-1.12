package mcjty.xnet.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Elec332 on 30-5-2016.
 */
public interface IXNetController extends INBTSerializable<NBTTagCompound> {

    public void addChannel(String name, IXNetChannel.Factory factory);

    public void removeChannel(UUID uuid);

    public void markDirty();

    //INTERNAL
    public void removeController();

    public Collection<IXNetChannel<?, ?>> getChannels();

}
