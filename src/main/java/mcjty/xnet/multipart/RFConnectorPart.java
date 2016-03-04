package mcjty.xnet.multipart;

import mcjty.xnet.init.ModItems;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class RFConnectorPart extends AbstractConnectorPart {

    public RFConnectorPart(EnumFacing side) {
        super(side);
    }

    public RFConnectorPart(){
        super();
    }

    @Override
    public String getModelPath() {
        return "xnet:rfconnector";
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.energyConnector);
    }
}
