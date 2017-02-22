package mcjty.xnet.blocks.facade;

import mcjty.lib.compat.CompatItemBlock;
import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.blocks.cables.NetCableSetup;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FacadeItemBlock extends CompatItemBlock {

    public FacadeItemBlock(FacadeBlock block) {
        super(block);
    }

    @Override
    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block != NetCableSetup.netCableBlock) {
            return EnumActionResult.FAIL;
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (ItemStackTools.isValid(itemstack)) {
            int i = this.getMetadata(itemstack.getMetadata());
            FacadeBlock facadeBlock = (FacadeBlock) this.block;
            IBlockState placementState = facadeBlock.getPlacementState(world, pos, facing, hitX, hitY, hitZ, i, player);

            if (placeBlockAt(itemstack, player, world, pos, facing, hitX, hitY, hitZ, placementState)) {
                // @todo
                SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
