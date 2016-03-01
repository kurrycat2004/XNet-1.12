package mcjty.xnet.api;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.PartSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Created by Elec332 on 1-3-2016.
 */
public class XNetAPIHelper {

    public static boolean canXNetConnect(ICapabilityProvider capabilityProvider, EnumFacing side){
        return capabilityProvider.hasCapability(XNetAPI.XNET_CAPABILITY, side);
    }

    public static IXNetComponent getXNet(ICapabilityProvider capabilityProvider, EnumFacing side){
        return capabilityProvider.getCapability(XNetAPI.XNET_CAPABILITY, side);
    }

    public static IXNetComponent getComponentAt(final TileEntity tile, final EnumFacing facing){
        if (tile == null){
            return null;
        }
        final EnumFacing fop = facing.getOpposite();
        if (tile instanceof IMultipartContainer){
            IMultipart mp = ((IMultipartContainer)tile).getPartInSlot(PartSlot.getFaceSlot(facing));
            if (mp instanceof ICapabilityProvider && canXNetConnect((ICapabilityProvider) mp, fop)){
                return getXNet((ICapabilityProvider) mp, fop);
            }
        }
        final TileEntity tileAt = tile.getWorld().getTileEntity(tile.getPos().offset(facing));
        if (tileAt == null){
            return null;
        }
        if (canXNetConnect(tileAt, fop)){
            return getXNet(tileAt, fop);
        }
        if (tileAt instanceof IMultipartContainer){
            IMultipart mp = ((IMultipartContainer)tile).getPartInSlot(PartSlot.CENTER);
            if (mp instanceof ICapabilityProvider && canXNetConnect((ICapabilityProvider) mp, fop)){
                return getXNet((ICapabilityProvider) mp, fop);
            }
        }
        return null;
    }

}
