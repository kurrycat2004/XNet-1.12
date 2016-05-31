package mcjty.xnet.api;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

/**
 * Created by Elec332 on 2-3-2016.
 */
public interface IXNetChannel<C extends IXNetComponent, S extends NBTBase> extends INBTSerializable<S> {

    /**
     * Called when a compatible object is added to the network
     *
     * @param object The object
     */
    public void addObject(C object);

    /**
     * Called when a object is removed to the network
     *
     * @param object The object
     */
    public void removeObject(C object);

    /**
     * Used to check if a certain object is compatible with this network.
     *
     * @param object The object
     * @return Whether the object is compatible with this network.
     */
    public boolean isValidObject(C object);

    /**
     * Gets the name of this channels.
     *
     * @return The name of this channels.
     */
    public String getName();

    /**
     * Sets the name of the channels.
     * The name should be saved to NBT.
     *
     * @param name The new name for this channels.
     */
    public void setName(String name);

    /**
     * Called just before the channels gets removed
     */
    public void invalidate();

    public abstract class Factory extends IForgeRegistryEntry.Impl<Factory> {

        public abstract IXNetChannel createNewChannel(IXNetController controller);

        public abstract Class<? extends IXNetChannel> getTypeClass();

        public abstract Item getRenderItem();

    }

}
