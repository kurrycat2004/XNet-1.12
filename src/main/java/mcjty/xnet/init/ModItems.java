package mcjty.xnet.init;

import mcjty.xnet.XNet;
import mcjty.xnet.multipart.XNetCableMultiPart;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static void init() {
        GameRegistry.registerItem(new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player) {
                return new XNetCableMultiPart();
            }
        }.setCreativeTab(XNet.tabXNet), "xnetmp");

    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
    }

}
