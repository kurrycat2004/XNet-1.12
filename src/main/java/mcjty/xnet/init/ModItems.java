package mcjty.xnet.init;

import mcjty.xnet.XNet;
import mcjty.xnet.multipart.RFConnectorPart;
import mcjty.xnet.multipart.XNetCableMultiPart;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    public static ItemMultiPart cable;
    public static ItemMultiPart energyConnector;


    public static void init() {
        cable = new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player) {
                return new XNetCableMultiPart();
            }
        };
        registerPartItem(cable, "netcable");
        energyConnector = new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3 hit, ItemStack stack, EntityPlayer player) {
                return new RFConnectorPart();
            }
        };
        registerPartItem(energyConnector, "energy_connector");
    }

    private static void registerPartItem(ItemMultiPart partItem, String name) {
        partItem.setCreativeTab(XNet.tabXNet);
        partItem.setRegistryName(name);
        partItem.setUnlocalizedName(name);
        GameRegistry.registerItem(partItem);
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        ModelLoader.setCustomModelResourceLocation(cable, 0, new ModelResourceLocation(cable.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(energyConnector, 0, new ModelResourceLocation(energyConnector.getRegistryName(), "inventory"));
    }

}
