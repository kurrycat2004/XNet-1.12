package mcjty.xnet.multipart;

import mcjty.xnet.api.IXNetComponent;
import mcjty.xnet.api.XNetAPI;
import mcjty.xnet.api.XNetAPIHelper;
import mcjty.xnet.varia.UnlistedPropertyBoolean;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.*;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.EnumSet;

/**
 * Created by Elec332 on 1-3-2016.
 */
public class XNetCableMultiPart extends Multipart implements ISlottedPart, IXNetComponent {

    // Properties that indicate if there is a connection to certain direction.
    public static final UnlistedPropertyBoolean NORTH = new UnlistedPropertyBoolean("north");
    public static final UnlistedPropertyBoolean SOUTH = new UnlistedPropertyBoolean("south");
    public static final UnlistedPropertyBoolean WEST = new UnlistedPropertyBoolean("west");
    public static final UnlistedPropertyBoolean EAST = new UnlistedPropertyBoolean("east");
    public static final UnlistedPropertyBoolean UP = new UnlistedPropertyBoolean("up");
    public static final UnlistedPropertyBoolean DOWN = new UnlistedPropertyBoolean("down");

    public XNetCableMultiPart(){
        this.connectedSides = EnumSet.noneOf(EnumFacing.class);
        this.id = -1;
    }

    private int id;
    private final EnumSet<EnumFacing> connectedSides;

    @Override
    public EnumSet<PartSlot> getSlotMask() {
        return EnumSet.of(PartSlot.CENTER);
    }

    @Override
    public String getModelPath() {
        return "xnet:i-aint-making-jsons";
    }

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        checkConnections();
    }

    @Override
    public void onPartChanged(IMultipart part) {
        super.onPartChanged(part);
        checkConnections();
    }

    @Override
    public BlockState createBlockState() {
        return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[0], new IUnlistedProperty[] { NORTH, SOUTH, WEST, EAST, UP, DOWN });
    }

    @Override
    public IBlockState getExtendedState(IBlockState state) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        checkConnections();
        boolean north = isConnected(EnumFacing.NORTH);
        boolean south = isConnected(EnumFacing.SOUTH);
        boolean west = isConnected(EnumFacing.WEST);
        boolean east = isConnected(EnumFacing.EAST);
        boolean up = isConnected(EnumFacing.UP);
        boolean down = isConnected(EnumFacing.DOWN);

        return extendedBlockState.withProperty(NORTH, north).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(EAST, east).withProperty(UP, up).withProperty(DOWN, down);
    }

    private boolean isConnected(EnumFacing facing){
        return connectedSides.contains(facing);
    }

    private void checkConnections(){ //TODO: Not check at rendering
        TileEntity me = getWorld().getTileEntity(getPos());
        connectedSides.clear();
        for (EnumFacing facing : EnumFacing.VALUES){
            if (XNetAPIHelper.getComponentAt(me, facing) != null){
                connectedSides.add(facing);
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
        markDirty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == XNetAPI.XNET_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == XNetAPI.XNET_CAPABILITY ? (T) this : super.getCapability(capability, facing);
    }

}
