package mcjty.xnet.init;

import mcjty.xnet.XNet;
import mcjty.xnet.cables.XNetAdvancedCableMultiPart;
import mcjty.xnet.cables.XNetCableMultiPart;
import mcjty.xnet.connectors.ItemConnectorPart;
import mcjty.xnet.connectors.RFConnectorPart;
import mcjty.xnet.terminal.TerminalPart;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    @SuppressWarnings("PublicField")
    public static ItemMultiPart cable;
    @SuppressWarnings("PublicField")
    public static ItemMultiPart advancedCable;
    @SuppressWarnings("PublicField")
    public static ItemMultiPart energyConnector;
    @SuppressWarnings("PublicField")
    public static ItemMultiPart itemConnector;
    @SuppressWarnings("PublicField")
    public static ItemMultiPart terminal;


    public static void init() {
        cable = registerPartItem(new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return new XNetCableMultiPart();
            }
        }, "netcable");
        advancedCable = registerPartItem(new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return new XNetAdvancedCableMultiPart();
            }
        }, "advanced_netcable");
        energyConnector = registerPartItem(new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return new RFConnectorPart(side);
            }
        }, "energy_connector");
        itemConnector = registerPartItem(new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return new ItemConnectorPart(side);
            }
        }, "item_connector");
        terminal = registerPartItem(new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return new TerminalPart(side);
            }
        }, "terminal");
    }

    private static ItemMultiPart registerPartItem(ItemMultiPart partItem, String name) {
        partItem.setCreativeTab(XNet.tabXNet);
        partItem.setRegistryName(name);
        partItem.setUnlocalizedName(name);
        GameRegistry.registerItem(partItem);
        return partItem;
    }

}
