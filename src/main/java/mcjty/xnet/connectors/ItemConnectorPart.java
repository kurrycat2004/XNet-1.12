package mcjty.xnet.connectors;

import mcjty.xnet.XNet;
import mcjty.xnet.client.GuiProxy;
import mcjty.xnet.client.XNetClientModelLoader;
import mcjty.xnet.init.ModItems;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConnectorPart extends AbstractConnectorPart {

    public ItemConnectorPart(EnumFacing side) {
        super(side);
    }

    public ItemConnectorPart(){
        super();
    }

    @Override
    public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
        return new ItemStack(ModItems.itemConnector);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getTexture(boolean front) {
        return XNetClientModelLoader.spriteItem;
    }

    @Override
    public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
        if (!getWorld().isRemote) {
            player.openGui(XNet.instance, GuiProxy.GUI_ITEMCONNECTOR, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
        }
        return true;
    }

}
