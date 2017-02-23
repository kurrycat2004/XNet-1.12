package mcjty.xnet.blocks.facade;

import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FacadeTileEntity extends GenericTileEntity {

    private IBlockState mimicBlock = Blocks.COBBLESTONE.getDefaultState();

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        IBlockState oldMimicBlock = mimicBlock;

        super.onDataPacket(net, packet);

        if (getWorld().isRemote) {
            // If needed send a render update.
            if (!mimicBlock.equals(oldMimicBlock)) {
                getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }
    }


    public IBlockState getMimicBlock() {
        return mimicBlock;
    }

    public void setMimicBlock(IBlockState mimicBlock) {
        this.mimicBlock = mimicBlock;
        markDirtyClient();
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        String regName = tagCompound.getString("regName");
        int meta = tagCompound.getInteger("meta");
        Block value = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(regName));
        if (value == null) {
            mimicBlock = Blocks.COBBLESTONE.getDefaultState();
        } else {
            mimicBlock = value.getStateFromMeta(meta);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setString("regName", mimicBlock.getBlock().getRegistryName().toString());
        tagCompound.setInteger("meta", mimicBlock.getBlock().getMetaFromState(mimicBlock));
        return tagCompound;
    }
}
