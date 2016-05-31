package mcjty.xnet.channels;

import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.IXNetChannel;
import mcjty.xnet.api.IXNetController;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 31-5-2016.
 */
public abstract class AbstractChannel<T extends IXNetComponent> implements IXNetChannel<T, NBTTagCompound> {

    public AbstractChannel(IXNetController controller){
        this.controller = controller;
        name = "<UNNAMED>";
    }

    private final IXNetController controller;
    private String name;

    /**
     * Gets the name of this channels.
     *
     * @return The name of this channels.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the channels.
     * The name should be saved to NBT.
     *
     * @param name The new name for this channels.
     */
    @Override
    public void setName(String name) {
        this.name = name;
        controller.markDirty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound save = new NBTTagCompound();
        save.setString("name", name);
        return save;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.name = nbt.getString("name");
    }

}
