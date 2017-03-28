package mcjty.xnet.blocks.redstoneproxy;

import mcjty.lib.compat.CompatBlock;
import mcjty.xnet.XNet;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedstoneProxyBlock extends CompatBlock {

    public RedstoneProxyBlock() {
        super(Material.IRON);
        setUnlocalizedName(XNet.MODID + ".redstone_proxy");
        setRegistryName("redstone_proxy");
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        setCreativeTab(XNet.tabXNet);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


}
