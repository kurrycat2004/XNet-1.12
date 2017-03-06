package mcjty.xnet.blocks.router;

import mcjty.lib.container.EmptyContainer;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.xnet.blocks.generic.GenericXNetBlock;
import mcjty.xnet.gui.GuiProxy;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RouterBlock extends GenericXNetBlock<TileEntityRouter, EmptyContainer> {

    public RouterBlock() {
        super(Material.IRON, TileEntityRouter.class, EmptyContainer.class, "router", false);
    }

    @Override
    public int getGuiID() {
        return GuiProxy.GUI_ROUTER;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return GuiRouter.class;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
    }
}
