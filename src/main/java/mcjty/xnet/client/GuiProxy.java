package mcjty.xnet.client;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericBlock;
import mcjty.xnet.connectors.GuiItemConnector;
import mcjty.xnet.connectors.GuiRFConnector;
import mcjty.xnet.terminal.GuiTerminal;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiProxy implements IGuiHandler {

    public static int GUI_TERMINAL = 1;
    public static int GUI_ITEMCONNECTOR = 2;
    public static int GUI_RFCONNECTOR = 3;


    @Override
    public Object getServerGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (guiid == GUI_TERMINAL || guiid == GUI_ITEMCONNECTOR || guiid == GUI_RFCONNECTOR) {
            return new EmptyContainer(entityPlayer);
        }

        BlockPos pos = new BlockPos(x, y, z);
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createServerContainer(entityPlayer, te);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int guiid, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (guiid == GUI_TERMINAL) {
            return new GuiTerminal(pos);
        }
        if (guiid == GUI_ITEMCONNECTOR) {
            return new GuiItemConnector(pos);
        }
        if (guiid == GUI_RFCONNECTOR) {
            return new GuiRFConnector(pos);
        }

        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof GenericBlock) {
            GenericBlock genericBlock = (GenericBlock) block;
            TileEntity te = world.getTileEntity(pos);
            return genericBlock.createClientGui(entityPlayer, te);
        }
        return null;
    }

}
