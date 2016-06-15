package mcjty.xnet.blocks;

import mcjty.lib.container.GenericBlock;
import mcjty.lib.container.GenericItemBlock;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.xnet.XNet;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class GenericXNetBlock<T extends GenericTileEntity, C extends Container> extends GenericBlock<T, C> {

    public GenericXNetBlock(Material material,
                            Class<? extends T> tileEntityClass,
                            Class<? extends C> containerClass,
                            String name, boolean isContainer) {
        this(material, tileEntityClass, containerClass, GenericItemBlock.class, name, isContainer);
    }

    public GenericXNetBlock(Material material,
                            Class<? extends T> tileEntityClass,
                            Class<? extends C> containerClass,
                            Class<? extends ItemBlock> itemBlockClass,
                            String name, boolean isContainer) {
        super(XNet.instance, material, tileEntityClass, containerClass, name, isContainer);
        setCreativeTab(XNet.tabXNet);
    }

    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            if ((!OrphaningCardItem.isPrivileged(player, world)) && (!player.getPersistentID().equals(genericTileEntity.getOwnerUUID()))) {
//                int securityChannel = genericTileEntity.getSecurityChannel();
//                if (securityChannel != -1) {
//                    SecurityChannels securityChannels = SecurityChannels.getChannels(world);
//                    SecurityChannels.SecurityChannel channels = securityChannels.getChannel(securityChannel);
//                    boolean playerListed = channels.getPlayers().contains(player.getDisplayNameString());
//                    if (channels.isWhitelist() != playerListed) {
//                        Logging.message(player, TextFormatting.RED + "You have no permission to use this block!");
//                        return true;
//                    }
//                }
//            }
        }
        return false;
    }


}
