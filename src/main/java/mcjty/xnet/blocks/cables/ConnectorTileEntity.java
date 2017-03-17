package mcjty.xnet.blocks.cables;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.xnet.blocks.facade.IFacadeSupport;
import mcjty.xnet.blocks.facade.MimicBlockSupport;
import mcjty.xnet.config.GeneralConfiguration;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Map;

public class ConnectorTileEntity extends GenericTileEntity implements IEnergyProvider, IEnergyReceiver,
        IFacadeSupport {

    public static final String CMD_SETNAME = "setName";

    private MimicBlockSupport mimicBlockSupport = new MimicBlockSupport();

    private int energy = 0;
    private int inputFromSide[] = new int[] { 0, 0, 0, 0, 0, 0 };
    private String name = "";

    private Block[] cachedNeighbours = new Block[EnumFacing.VALUES.length];

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        IBlockState oldMimicBlock = mimicBlockSupport.getMimicBlock();

        super.onDataPacket(net, packet);

        if (getWorld().isRemote) {
            // If needed send a render update.
            if (mimicBlockSupport.getMimicBlock() != oldMimicBlock) {
                getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
            }
        }
    }


    @Override
    public IBlockState getMimicBlock() {
        return mimicBlockSupport.getMimicBlock();
    }

    public void setMimicBlock(IBlockState mimicBlock) {
        mimicBlockSupport.setMimicBlock(mimicBlock);
        markDirtyClient();
    }

    // Optimization to only increase the network if there is an actual block change
    public void possiblyMarkNetworkDirty(@Nonnull BlockPos neighbor) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (getPos().offset(facing).equals(neighbor)) {
                Block newblock = getWorld().getBlockState(neighbor).getBlock();
                if (newblock != cachedNeighbours[facing.ordinal()]) {
                    cachedNeighbours[facing.ordinal()] = newblock;
                    WorldBlob worldBlob = XNetBlobData.getBlobData(getWorld()).getWorldBlob(getWorld());
                    worldBlob.incNetworkVersion(worldBlob.getNetworkAt(getPos()));
                }
                return;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        energy = tagCompound.getInteger("energy");
        inputFromSide = tagCompound.getIntArray("inputs");
        if (inputFromSide.length != 6) {
            inputFromSide = new int[] { 0, 0, 0, 0, 0, 0 };
        }
        name = tagCompound.getString("name");
        mimicBlockSupport.readFromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("energy", energy);
        tagCompound.setIntArray("inputs", inputFromSide);
        tagCompound.setString("name", name);
        mimicBlockSupport.writeToNBT(tagCompound);
        return tagCompound;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public String getConnectorName() {
        return name;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        if (this.energy != energy) {
            if (energy < 0) {
                energy = 0;
            }
            this.energy = energy;
            markDirtyQuick();
        }
    }

    public void setEnergyInputFrom(EnumFacing from, int rate) {
        if (inputFromSide[from.ordinal()] != rate) {
            inputFromSide[from.ordinal()] = rate;
            markDirtyQuick();
        }
    }

    public int getMaxEnergy() {
        return GeneralConfiguration.maxRfConnector;
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        if (from == null) {
            return 0;
        }
        int m = inputFromSide[from.ordinal()];
        if (m > 0) {
            int toreceive = Math.min(maxReceive, m);
            int newenergy = energy + toreceive;
            if (newenergy > getMaxEnergy()) {
                toreceive -= newenergy - getMaxEnergy();
                newenergy = getMaxEnergy();
            }
            if (!simulate && energy != newenergy) {
                energy = newenergy;
                inputFromSide[from.ordinal()] = 0;
                markDirtyQuick();
            }
            return toreceive;
        }
        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        if (from == null) {
            return false;
        }
        return inputFromSide[from.ordinal()] > 0;
    }

    private IEnergyStorage[] sidedHandlers = new IEnergyStorage[6];

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_SETNAME.equals(command)) {
            this.name = args.get("name").getString();
            markDirtyClient();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && facing != null) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == null) {
                return null;
            } else {
                if (sidedHandlers[facing.ordinal()] == null) {
                    createSidedHandler(facing);
                }
                return CapabilityEnergy.ENERGY.cast(sidedHandlers[facing.ordinal()]);
            }
        }
        return super.getCapability(capability, facing);
    }

    private void createSidedHandler(EnumFacing facing) {
        sidedHandlers[facing.ordinal()] = new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return ConnectorTileEntity.this.receiveEnergy(facing, maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return ConnectorTileEntity.this.getEnergyStored(facing);
            }

            @Override
            public int getMaxEnergyStored() {
                return ConnectorTileEntity.this.getMaxEnergyStored(facing);
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

}
