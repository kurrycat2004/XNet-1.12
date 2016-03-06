package mcjty.xnet.api;

import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 6-3-2016.
 */
public interface IXNetCable {

    /**
     * This checks whether this cable can connect to the given cable
     *
     * @param otherCable The cable to check compatibility with
     * @return Whether this cable can connect to the other cable
     */
    public boolean canConnectTo(IXNetCable otherCable);

    /**
     * Since most MCMP cables will occupy SlotPart.MIDDLE, we cannot check capabilities for
     * a certain side (that will ignore the MultiPart in the "MIDDLE" slot), so we make sure
     * it can connect here.
     *
     * @param facing The side to be checked
     * @return Whether the cable can connect to the given side (discarding what is there, that is done by canConnectTo above)
     */
    public boolean canConnectToSide(EnumFacing facing);

}
