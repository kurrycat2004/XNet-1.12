package mcjty.xnet.cables;

import mcjty.xnet.init.ModItems;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class XNetAdvancedCableMultiPart extends AbstractCableMultiPart {

    private static final AxisAlignedBB[] HITBOXES;

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.advancedCable);
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }

    @Override
    protected AxisAlignedBB[] getHitBoxes() {
        return HITBOXES;
    }

    static {
        HITBOXES = new AxisAlignedBB[7];
        float thickness = .4f;
        float heightStuff = (1 - thickness)/2;
        float f1 = thickness + heightStuff;
        HITBOXES[EnumFacing.DOWN.ordinal()] = new AxisAlignedBB(heightStuff, heightStuff, heightStuff, f1, 0, f1);
        HITBOXES[EnumFacing.UP.ordinal()] = new AxisAlignedBB(heightStuff, 1, heightStuff, f1, f1, f1);
        HITBOXES[EnumFacing.NORTH.ordinal()] = new AxisAlignedBB(heightStuff, heightStuff, heightStuff, f1, f1, 0);
        HITBOXES[EnumFacing.SOUTH.ordinal()] = new AxisAlignedBB(heightStuff, heightStuff, 1, f1, f1, f1);
        HITBOXES[EnumFacing.WEST.ordinal()] = new AxisAlignedBB(heightStuff, heightStuff, heightStuff, 0, f1, f1);
        HITBOXES[EnumFacing.EAST.ordinal()] = new AxisAlignedBB(1, heightStuff, heightStuff, f1, f1, f1);
        HITBOXES[6] = new AxisAlignedBB(heightStuff, heightStuff, heightStuff, f1, f1, f1);
    }

}