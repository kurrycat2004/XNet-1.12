package mcjty.xnet.api;

import mcjty.xnet.api.channels.IChannelType;
import mcjty.xnet.api.channels.IConnectable;

import javax.annotation.Nonnull;

/**
 * Main interface for XNet.
 * Get a reference to an implementation of this interface by calling:
 *         FMLInterModComms.sendFunctionMessage("xnet", "getXNet", "<whatever>.YourClass$GetXNet");
 */
public interface IXNet {

    void registerChannelType(IChannelType type);

    /**
     * Register a connectable implementation. You can use this instead of implementing IConnectable
     * on your block. The connectable interface will have the responsability of checking if it is
     * being called on the right block
     */
    void registerConnectable(@Nonnull IConnectable connectable);
}
