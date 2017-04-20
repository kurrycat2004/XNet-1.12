package mcjty.xnet.items;

import mcjty.lib.compat.CompatItem;
import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.XNet;
import mcjty.xnet.api.keys.ConsumerId;
import mcjty.xnet.blocks.cables.ConnectorBlock;
import mcjty.xnet.blocks.cables.ConnectorTileEntity;
import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.blocks.generic.CableColor;
import mcjty.xnet.blocks.generic.GenericCableBlock;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ConnectorUpgradeItem extends CompatItem {

    public ConnectorUpgradeItem() {
        setUnlocalizedName(XNet.MODID + ".connector_upgrade");
        setRegistryName("connector_upgrade");
        setCreativeTab(XNet.tabXNet);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.clAddInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.BLUE + "Sneak right click this on a");
        tooltip.add(TextFormatting.BLUE + "normal connector to upgrade it");
        tooltip.add(TextFormatting.BLUE + "to an advanced connector");
    }

    @Override
    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block == NetCableSetup.connectorBlock) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof ConnectorTileEntity) {
                    NBTTagCompound tag = new NBTTagCompound();
                    te.writeToNBT(tag);
                    CableColor color = world.getBlockState(pos).getValue(GenericCableBlock.COLOR);

                    XNetBlobData blobData = XNetBlobData.getBlobData(world);
                    WorldBlob worldBlob = blobData.getWorldBlob(world);
                    ConsumerId consumer = worldBlob.getConsumerAt(pos);
                    ((ConnectorBlock)block).unlinkBlock(world, pos);
                    world.setBlockState(pos, NetCableSetup.advancedConnectorBlock.getDefaultState().withProperty(GenericCableBlock.COLOR, color));
                    IBlockState blockState = world.getBlockState(pos);
                    ((ConnectorBlock)blockState.getBlock()).createCableSegment(world, pos, consumer);

                    te = TileEntity.create(world, tag);
                    if (te != null) {
                        world.getChunkFromBlockCoords(pos).addTileEntity(te);
                        te.markDirty();
                        world.notifyBlockUpdate(pos, blockState, blockState, 3);
                        player.inventory.decrStackSize(player.inventory.currentItem, 1);
                        player.openContainer.detectAndSendChanges();
                        ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.GREEN + "Connector was upgraded"));
                    } else {
                        ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.RED + "Something went wrong during upgrade!"));
                        return EnumActionResult.FAIL;
                    }
                }
            }
            return EnumActionResult.SUCCESS;
        } else if (block == NetCableSetup.advancedConnectorBlock) {
            if (!world.isRemote) {
                ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.YELLOW + "This connector is already advanced!"));
            }
            return EnumActionResult.SUCCESS;
        } else {
            if (!world.isRemote) {
                ChatTools.addChatMessage(player, new TextComponentString(TextFormatting.RED + "Use this item on a connector to upgrade it!"));
            }
            return EnumActionResult.SUCCESS;
        }
    }

}
