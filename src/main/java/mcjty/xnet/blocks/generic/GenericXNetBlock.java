package mcjty.xnet.blocks.generic;

import mcjty.lib.blocks.GenericBlock;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.xnet.XNet;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

public abstract class GenericXNetBlock<T extends GenericTileEntity, C extends Container> extends GenericBlock<T, C> {

    public GenericXNetBlock(Material material, Class<? extends T> tileEntityClass,
                            BiFunction<EntityPlayer, IInventory, C> containerFactory,
                            Class<? extends ItemBlock> itemBlockClass, String name, boolean isContainer) {
        super(XNet.instance, material, tileEntityClass, containerFactory, itemBlockClass, name, isContainer);
        setCreativeTab(XNet.tabXNet);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
