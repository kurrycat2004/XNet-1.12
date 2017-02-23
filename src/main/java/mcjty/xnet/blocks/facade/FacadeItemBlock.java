package mcjty.xnet.blocks.facade;

import mcjty.lib.compat.CompatItemBlock;
import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.blocks.cables.NetCableSetup;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class FacadeItemBlock extends CompatItemBlock {

    public FacadeItemBlock(FacadeBlock block) {
        super(block);
    }

    public static void setMimicBlock(@Nonnull ItemStack item, IBlockState mimicBlock) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("regName", mimicBlock.getBlock().getRegistryName().toString());
        tagCompound.setInteger("meta", mimicBlock.getBlock().getMetaFromState(mimicBlock));
        item.setTagCompound(tagCompound);
    }

    public static IBlockState getMimicBlock(@Nonnull ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null || !tagCompound.hasKey("regName")) {
            return Blocks.COBBLESTONE.getDefaultState();
        } else {
            String regName = tagCompound.getString("regName");
            int meta = tagCompound.getInteger("meta");
            Block value = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(regName));
            return value.getStateFromMeta(meta);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null || !tagCompound.hasKey("regName")) {
            tooltip.add(TextFormatting.BLUE + "Right click on block to mimic");
        } else {
            String regName = tagCompound.getString("regName");
            int meta = tagCompound.getInteger("meta");
            Block value = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(regName));
            if (value != null) {
                ItemStack s = new ItemStack(value, 1, meta);
                tooltip.add(TextFormatting.BLUE + "Mimicing " + s.getDisplayName());
            }
        }
    }

    @Override
    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ItemStack itemstack = player.getHeldItem(hand);

        if (ItemStackTools.isValid(itemstack)) {

            if (block != NetCableSetup.netCableBlock) {
                setMimicBlock(itemstack, state);
                ChatTools.addChatMessage(player, new TextComponentString("Facade is now mimicing " + block.getLocalizedName()));
                return EnumActionResult.SUCCESS;
            }

            int i = this.getMetadata(itemstack.getMetadata());
            FacadeBlock facadeBlock = (FacadeBlock) this.block;
            IBlockState placementState = facadeBlock.getPlacementState(world, pos, facing, hitX, hitY, hitZ, i, player);

            if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, placementState)) {
                // @todo
                SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof FacadeTileEntity) {
                    ((FacadeTileEntity) te).setMimicBlock(getMimicBlock(itemstack));
                }
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
