package mcjty.xnet.multiblock;

import mcjty.xnet.blocks.GenericCableTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CableNetwork extends WorldSavedData {

    public static final String CABLENETWORK_NAME = "XNetCableNetwork";
    private static CableNetwork instance = null;

    private int lastId = 0;

    private final Map<Integer,Network> networks = new HashMap<>();

    public CableNetwork(String identifier) {
        super(identifier);
    }

    public void save(World world) {
        world.setItemData(CABLENETWORK_NAME, this);
        markDirty();
    }

    public static void clearInstance() {
        if (instance != null) {
            instance.networks.clear();
            instance = null;
        }
    }

    public static CableNetwork getChannels() {
        return instance;
    }

    public static CableNetwork getChannels(World world) {
        if (world.isRemote) {
            return null;
        }
        if (instance != null) {
            return instance;
        }
        instance = (CableNetwork) world.loadItemData(CableNetwork.class, CABLENETWORK_NAME);
        if (instance == null) {
            instance = new CableNetwork(CABLENETWORK_NAME);
        }
        return instance;
    }

    public Network getOrCreateNetwork(int id) {
        Network channel = networks.get(id);
        if (channel == null) {
            channel = new Network();
            networks.put(id, channel);
        }
        return channel;
    }

    public Network getChannel(int id) {
        return networks.get(id);
    }

    public void deleteChannel(int id) {
        networks.remove(id);
    }

    public int newChannel() {
        lastId++;
        return lastId;
    }

    public void moveNetwork(World world, int fromId, int toId) {
        Network toNetwork = networks.get(toId);
        Network fromNetwork = networks.get(fromId);
        for (BlockPos pos : fromNetwork.blocks) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof GenericCableTileEntity) {
                GenericCableTileEntity genericCableTileEntity = (GenericCableTileEntity) te;
                genericCableTileEntity.setId(toId);
                toNetwork.add(pos);
                fromNetwork.remove(pos);
            }
        }
        save(world);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        networks.clear();
        NBTTagList lst = tagCompound.getTagList("networks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < lst.tagCount() ; i++) {
            NBTTagCompound tc = lst.getCompoundTagAt(i);
            int channel = tc.getInteger("channel");
            Network value = new Network();
            value.readFromNBT(tc);
            networks.put(channel, value);
        }
        lastId = tagCompound.getInteger("lastId");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagList lst = new NBTTagList();
        for (Map.Entry<Integer, Network> entry : networks.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("channel", entry.getKey());
            entry.getValue().writeToNBT(tc);
            lst.appendTag(tc);
        }
        tagCompound.setTag("networks", lst);
        tagCompound.setInteger("lastId", lastId);
    }

    public static class Network {
        private Set<BlockPos> blocks = new HashSet<>();

        // Be careful with this! Don't modify the set
        public Set<BlockPos> getBlocks() {
            return blocks;
        }

        public int getBlockCount() {
            return blocks.size();
        }

        public void add(BlockPos g) {
            if (!blocks.contains(g)) {
                blocks.add(g);
            }
        }

        public void remove(BlockPos g) {
            if (blocks.contains(g)) {
                blocks.remove(g);
            }
        }

        public void writeToNBT(NBTTagCompound tagCompound){
            NBTTagList list = new NBTTagList();
            for (BlockPos block : blocks) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("x", block.getX());
                tag.setInteger("y", block.getY());
                tag.setInteger("z", block.getZ());
                list.appendTag(tag);
            }

            tagCompound.setTag("blocks", list);
        }

        public void readFromNBT(NBTTagCompound tagCompound){
            blocks.clear();
            NBTTagList list = tagCompound.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
            for (int i = 0 ; i < list.tagCount() ; i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                blocks.add(new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
            }
        }
    }
}
