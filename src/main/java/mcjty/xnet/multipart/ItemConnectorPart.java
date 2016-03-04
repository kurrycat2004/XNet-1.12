package mcjty.xnet.multipart;

import mcjty.xnet.init.ModItems;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class ItemConnectorPart extends AbstractConnectorPart {

    public ItemConnectorPart(EnumFacing side) {
        super(side);
    }

    public ItemConnectorPart(){
        super();
    }

    @Override
    public String getModelPath() {
        return "xnet:itemconnector";
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.itemConnector);
    }
}
