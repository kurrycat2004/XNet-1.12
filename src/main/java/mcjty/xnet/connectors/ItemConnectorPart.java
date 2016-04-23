package mcjty.xnet.connectors;

import mcjty.xnet.init.ModItems;
import mcjty.xnet.varia.XNetResourceLocation;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class ItemConnectorPart extends AbstractConnectorPart {

    public ItemConnectorPart(EnumFacing side) {
        super(side);
    }

    public ItemConnectorPart(){
        super();
    }


    @Override
    public ResourceLocation getModelPath() {
        return new XNetResourceLocation("itemconnector");
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.itemConnector);
    }
}
