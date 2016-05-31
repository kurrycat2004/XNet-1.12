package mcjty.xnet.terminal;

import mcjty.xnet.XNet;
import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.client.GuiProxy;
import mcjty.xnet.client.XNetClientModelLoader;
import mcjty.xnet.client.model.IConnectorRenderable;
import mcjty.xnet.connectors.AbstractConnectorPart;
import mcjty.xnet.init.ModItems;
import mcjty.xnet.varia.CommonProperties;
import mcmultipart.MCMultiPartMod;
import mcmultipart.client.multipart.ICustomHighlightPart;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;
import java.util.List;

public class TerminalPart extends AbstractConnectorPart {

    public TerminalPart(EnumFacing side){
        super(side);
    }

    public TerminalPart(){
        super();
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.terminal);
    }

    @Override
    public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
        if (!getWorld().isRemote) {
            player.openGui(XNet.instance, GuiProxy.GUI_TERMINAL, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTexture(boolean front) {
        return front ? XNetClientModelLoader.spriteTerminal : XNetClientModelLoader.spriteSide;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderFront() {
        return true;
    }

}
