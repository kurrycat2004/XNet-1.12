package mcjty.xnet.channel;

import net.minecraft.nbt.NBTTagCompound;

public class Channel {

    private String name;

    public Channel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("name", name);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        name = tagCompound.getString("name");
    }
}
