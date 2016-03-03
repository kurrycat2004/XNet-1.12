package mcjty.xnet.multipart;

import mcjty.xnet.XNet;
import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.varia.UnlistedPropertySide;
import mcmultipart.MCMultiPartMod;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.EnumSet;

/**
 * Created by Elec332 on 1-3-2016.
 */
public abstract class AbstractConnectorPart extends Multipart implements ISlottedPart, IXNetComponent {

    public static final UnlistedPropertySide SIDE = new UnlistedPropertySide("side");

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
    public BlockState createBlockState() {
        return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[0], new IUnlistedProperty[] { SIDE });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        return extendedBlockState.withProperty(SIDE, side);
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

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == XNetAPI.XNET_CAPABILITY && facing == side.getOpposite() || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == XNetAPI.XNET_CAPABILITY && facing == side.getOpposite() ? (T)this :super.getCapability(capability, facing);
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
