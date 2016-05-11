package mcjty.xnet.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Created by Elec332 on 1-3-2016.
 */
public class XNetAPI {

    @CapabilityInject(IXNetComponent.class)
    public static Capability<IXNetComponent> XNET_CAPABILITY;

    @CapabilityInject(IXNetComponent.class)
    public static Capability<IXNetCable> XNET_CABLE_CAPABILITY;

    public static void dummyLoad(){
    }

    static {
        registerCapability(IXNetComponent.class);
    }

    private static <C extends IXNetComponent> void registerCapability(Class<C> clazz){
        CapabilityManager.INSTANCE.register(clazz, new Capability.IStorage<C>() {
            @Override
            public NBTBase writeNBT(Capability<C> capability, C instance, EnumFacing side) {
                NBTTagCompound tag = new NBTTagCompound(); //In case we need to store more than just an int later
                tag.setInteger("net_ID", instance.getId());
                return null;
            }

            @Override
            public void readNBT(Capability<C> capability, C instance, EnumFacing side, NBTBase nbt) {
                instance.setId(((NBTTagCompound)nbt).getInteger("net_ID"));
            }
        }, () -> {
            throw new UnsupportedOperationException();
        });
        CapabilityManager.INSTANCE.register(IXNetCable.class, new Capability.IStorage<IXNetCable>() {
            @Override
            public NBTBase writeNBT(Capability<IXNetCable> capability, IXNetCable instance, EnumFacing side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability<IXNetCable> capability, IXNetCable instance, EnumFacing side, NBTBase nbt) {
                throw new UnsupportedOperationException();
            }
        }, () -> {
            throw new UnsupportedOperationException();
        });
    }


}
