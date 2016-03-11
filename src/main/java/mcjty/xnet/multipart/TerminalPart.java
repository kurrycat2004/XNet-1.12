package mcjty.xnet.multipart;

import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.init.ModItems;
import mcjty.xnet.varia.UnlistedPropertySide;
import mcmultipart.MCMultiPartMod;
import mcmultipart.client.multipart.ICustomHighlightPart;
import mcmultipart.multipart.IOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;
import java.util.List;

public class TerminalPart extends Multipart implements ISlottedPart, IXNetComponent, IOccludingPart, ICustomHighlightPart {

    public static final UnlistedPropertySide SIDE = new UnlistedPropertySide("side");

    private static final AxisAlignedBB[] HITBOXES;

    public TerminalPart(EnumFacing side){
        this();
        this.side = side;
    }

    public TerminalPart(){
        this.id = -1;
    }

    private EnumFacing side;
    private int id;


    @Override
    public String getModelPath() {
        return "xnet:terminal";
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.terminal);
    }

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
    public void writeUpdatePacket(PacketBuffer buf) {
        super.writeUpdatePacket(buf);
        buf.writeByte(side.ordinal());
    }

    @Override
    public void readUpdatePacket(PacketBuffer buf) {
        super.readUpdatePacket(buf);
        this.side = EnumFacing.values()[buf.readByte()];
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        //noinspection ObjectEquality
        return capability == XNetAPI.XNET_CAPABILITY && facing == side.getOpposite() || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        //noinspection ObjectEquality
        return capability == XNetAPI.XNET_CAPABILITY && facing == side.getOpposite() ? (T)this :super.getCapability(capability, facing);
    }

    @Override
    public void addSelectionBoxes(List<AxisAlignedBB> list) {
        list.add(HITBOXES[side.ordinal()]);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    @Override
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB hitbox = HITBOXES[side.ordinal()];
        if (mask.intersectsWith(hitbox)){
            list.add(hitbox);
        }
    }

    @Override
    public void addOcclusionBoxes(List<AxisAlignedBB> list) {
        list.add(HITBOXES[side.ordinal()]);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean drawHighlight(PartMOP hit, EntityPlayer player, ItemStack stack, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        AxisAlignedBB aabb = HITBOXES[side.ordinal()];
        RenderGlobal.drawSelectionBoundingBox(aabb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)/*.offset(-d0, -d1, -d2)*/);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        return true;
    }

    @Override
    public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
        return layer == EnumWorldBlockLayer.CUTOUT;
    }

    static {
        HITBOXES = new AxisAlignedBB[6];
        float thickness = .1f;
        float dfl = .2f;
        float f1 = 1 - thickness;
        float dflf1 = 1 - dfl;
        HITBOXES[EnumFacing.DOWN.ordinal()] = new AxisAlignedBB(dflf1, 0, dflf1, dfl, thickness, dfl);
        HITBOXES[EnumFacing.UP.ordinal()] = new AxisAlignedBB(dflf1, 1, dflf1, dfl, f1, dfl);
        HITBOXES[EnumFacing.NORTH.ordinal()] = new AxisAlignedBB(dflf1, dflf1, 0, dfl, dfl, thickness);
        HITBOXES[EnumFacing.SOUTH.ordinal()] = new AxisAlignedBB(dflf1, dflf1, 1, dfl, dfl, f1);
        HITBOXES[EnumFacing.WEST.ordinal()] = new AxisAlignedBB(0, dflf1, dflf1, thickness, dfl, dfl);
        HITBOXES[EnumFacing.EAST.ordinal()] = new AxisAlignedBB(1, dflf1, dflf1, f1, dfl, dfl);
    }

}
