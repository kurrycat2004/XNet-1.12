package mcjty.xnet.multipart;

import mcjty.xnet.XNet;
import mcjty.xnet.api.IXNetComponent;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * Created by Elec332 on 1-3-2016.
 */
public abstract class AbstractConnectorPart extends Multipart implements ISlottedPart, IXNetComponent {

    public AbstractConnectorPart(EnumFacing side){
        this();
        this.side = side;
    }

    public AbstractConnectorPart(){
        this.id = -1;
    }

    private EnumFacing side;
    private int id;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        //If the side is null we'll have more issues upon load than a crash now...
        tag.setString("side", side.getName());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.side = EnumFacing.byName(tag.getString("side"));
    }

    @Override
    public EnumSet<PartSlot> getSlotMask() {
        return EnumSet.of(PartSlot.getFaceSlot(side));
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
        markDirty();
    }

    public static Item generateItem(final Class<? extends AbstractConnectorPart> clazz){
        return new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player) {
                try {
                    return clazz.getConstructor(EnumFacing.class).newInstance(side);
                } catch (Exception e){
                    throw new RuntimeException("Error creating part, couldn't find a constructor with an EnumFacing argument...", e);
                }
            }
        }.setCreativeTab(XNet.tabXNet);
    }

}
