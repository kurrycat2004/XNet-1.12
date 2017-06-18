package mcjty.xnet.varia;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;

public class FluidTools {

    public static FluidStack convertBucketToFluid(@Nonnull ItemStack bucket) {
        IFluidHandler fluidHandler = FluidUtil.getFluidHandler(bucket);
        if (fluidHandler == null) {
            return null;
        }
        IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
        for (IFluidTankProperties properties : tankProperties) {
            FluidStack contents = properties.getContents();
            if (contents != null) {
                return contents;
            }
        }

        return null;
    }
}
