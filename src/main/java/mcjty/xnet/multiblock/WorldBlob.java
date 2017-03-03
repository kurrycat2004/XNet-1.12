package mcjty.xnet.multiblock;

import mcjty.xnet.api.keys.ConsumerId;
import mcjty.xnet.api.keys.NetworkId;
import mcjty.xnet.logic.VersionNumber;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class WorldBlob {

    private final int dimId;
    private final Map<Long, ChunkBlob> chunkBlobMap = new HashMap<>();
    private int lastNetworkId = 0;              // Network ID
    private int lastConsumerId = 0;             // Network consumer ID

    // All consumers (as position) for a given network. If an entry in this map does not
    // exist for a certain network that means the information has to be calculated
    private final Map<NetworkId, Set<BlockPos>> consumersOnNetwork = new HashMap<>();

    // For every network we maintain a version number. If something on a network changes
    // this increases and so things that depend on network topology can detect this change
    // and do the needed updates
    private final Map<NetworkId, VersionNumber> networkVersions = new HashMap<>();


    public WorldBlob(int dimId) {
        this.dimId = dimId;
    }

    public int getDimId() {
        return dimId;
    }

    @Nonnull
    public NetworkId newNetwork() {
        lastNetworkId++;
        return new NetworkId(lastNetworkId);
    }

    @Nonnull
    public ConsumerId newConsumer() {
        lastConsumerId++;
        return new ConsumerId(lastConsumerId);
    }

    @Nullable
    public BlobId getBlobAt(@Nonnull BlockPos pos) {
        ChunkBlob blob = getBlob(pos);
        if (blob == null) {
            return null;
        }
        IntPos intPos = new IntPos(pos);
        return blob.getBlobIdForPosition(intPos);
    }

    @Nullable
    public Set<NetworkId> getNetworksAt(@Nonnull BlockPos pos) {
        ChunkBlob blob = getBlob(pos);
        if (blob == null) {
            return null;
        }
        IntPos intPos = new IntPos(pos);
        return blob.getNetworksForPosition(intPos);
    }

    @Nullable
    public ConsumerId getConsumerAt(@Nonnull BlockPos pos) {
        ChunkBlob blob = getBlob(pos);
        if (blob == null) {
            return null;
        }
        IntPos intPos = new IntPos(pos);
        return blob.getNetworkConsumers().get(intPos);
    }

    @Nullable
    public ColorId getColorAt(@Nonnull BlockPos pos) {
        ChunkBlob blob = getBlob(pos);
        if (blob == null) {
            return null;
        }
        IntPos intPos = new IntPos(pos);
        return blob.getColorIdForPosition(intPos);
    }

    @Nonnull
    public Set<BlockPos> getConsumers(NetworkId network) {
        if (!consumersOnNetwork.containsKey(network)) {
            Set<BlockPos> positions = new HashSet<>();

            // @todo can this be done more optimal instead of traversing all the chunk blobs?
            for (ChunkBlob blob : chunkBlobMap.values()) {
                Set<IntPos> consumersForNetwork = blob.getConsumersForNetwork(network);
                for (IntPos intPos : consumersForNetwork) {
                    BlockPos pos = blob.getPosition(intPos);
                    positions.add(pos);
                }
            }
            consumersOnNetwork.put(network, positions);
        }
        return consumersOnNetwork.get(network);
    }

    private void removeCachedNetworksForBlob(ChunkBlob blob) {
        for (NetworkId id : blob.getNetworks()) {
            consumersOnNetwork.remove(id);
            incNetworkVersion(id);
        }
    }

    private void incNetworkVersion(NetworkId id) {
        if (!networkVersions.containsKey(id)) {
            networkVersions.put(id, new VersionNumber(1));
        }
        networkVersions.get(id).inc();
    }

    public int getNetworkVersion(NetworkId id) {
        if (!networkVersions.containsKey(id)) {
            return 0;
        } else {
            return networkVersions.get(id).getVersion();
        }
    }

    /**
     * Create a cable segment that is also a network provider at this section
     */
    public void createNetworkProvider(BlockPos pos, ColorId color, NetworkId network) {
        ChunkBlob blob = getOrCreateBlob(pos);
        blob.createNetworkProvider(pos, color, network);
        recalculateNetwork(blob);
    }

    /**
     * Create a cable segment that is also a network consumer at this section
     */
    public void createNetworkConsumer(BlockPos pos, ColorId color, ConsumerId consumer) {
        ChunkBlob blob = getOrCreateBlob(pos);
        blob.createNetworkConsumer(pos, color, consumer);
        recalculateNetwork(blob);
    }

    /**
     * Create a cable segment at a position
     */
    public void createCableSegment(BlockPos pos, ColorId color) {
        ChunkBlob blob = getOrCreateBlob(pos);
        if (blob.createCableSegment(pos, color)) {
            recalculateNetwork(blob);
        } else {
            recalculateNetwork(blob);
            // @todo optimize this case?
//            blob.fixNetworkAllocations();
//            removeCachedNetworksForBlob(blob);
        }
    }

    @Nonnull
    private ChunkBlob getOrCreateBlob(BlockPos pos) {
        ChunkPos cpos = new ChunkPos(pos);
        long chunkId = ChunkPos.asLong(cpos.chunkXPos, cpos.chunkZPos);
        if (!chunkBlobMap.containsKey(chunkId)) {
            chunkBlobMap.put(chunkId, new ChunkBlob(cpos));
        }
        return chunkBlobMap.get(chunkId);
    }

    @Nullable
    private ChunkBlob getBlob(BlockPos pos) {
        ChunkPos cpos = new ChunkPos(pos);
        long chunkId = ChunkPos.asLong(cpos.chunkXPos, cpos.chunkZPos);
        return chunkBlobMap.get(chunkId);
    }

    public void removeCableSegment(BlockPos pos) {
        ChunkBlob blob = getOrCreateBlob(pos);
        blob.removeCableSegment(pos);
        recalculateNetwork();
    }

    /**
     * Recalculate the network starting from the given block
     */
    public void recalculateNetwork(ChunkBlob blob) {
        removeCachedNetworksForBlob(blob);
        blob.fixNetworkAllocations();
        removeCachedNetworksForBlob(blob);

        Set<ChunkBlob> todo = new HashSet<>();
        Set<ChunkBlob> recalculated = new HashSet<>();  // Keep track of which chunks we already recalculated
        recalculated.add(blob);
        todo.add(blob);
        recalculateNetwork(todo, recalculated);
    }

    /**
     * Recalculate the entire network
     */
    public void recalculateNetwork() {
        // First make sure that every chunk has its network mappings correct (mapping
        // from blob id to network id). Note that this will discard all networking
        // information from neighbouring chunks. recalculateNetwork() should fix those.
        for (ChunkBlob blob : chunkBlobMap.values()) {
            blob.fixNetworkAllocations();
            removeCachedNetworksForBlob(blob);
        }

        // For every chunk we check all border positions and see where they connect with
        // adjacent chunks
        Set<ChunkBlob> todo = new HashSet<>(chunkBlobMap.values());
        recalculateNetwork(todo, null);
    }

    private void recalculateNetwork(@Nonnull Set<ChunkBlob> todo, @Nullable Set<ChunkBlob> recalculated) {
        while (!todo.isEmpty()) {
            ChunkBlob blob = todo.iterator().next();
            todo.remove(blob);
            if (recalculated != null) {
                if (!recalculated.contains(blob)) {
                    blob.fixNetworkAllocations();
                    recalculated.add(blob);
                }
            }
            removeCachedNetworksForBlob(blob);


            Set<IntPos> borderPositions = blob.getBorderPositions();
            ChunkPos chunkPos = blob.getChunkPos();
            for (IntPos pos : borderPositions) {
                Set<NetworkId> networks = blob.getOrCreateNetworksForPosition(pos);
                ColorId color = blob.getColorIdForPosition(pos);

                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (pos.isBorder(facing)) {
                        Vec3i vec = facing.getDirectionVec();
                        ChunkBlob adjacent = chunkBlobMap.get(
                                ChunkPos.asLong(chunkPos.chunkXPos+vec.getX(), chunkPos.chunkZPos+vec.getZ()));
                        if (adjacent != null) {
                            IntPos connectedPos = pos.otherSide(facing);
                            if (adjacent.getBorderPositions().contains(connectedPos) && adjacent.getColorIdForPosition(connectedPos).equals(color)) {
                                // We have a connection!
                                Set<NetworkId> adjacentNetworks = adjacent.getOrCreateNetworksForPosition(connectedPos);
                                if (networks.addAll(adjacentNetworks)) {
                                    todo.add(blob);     // We changed this blob so need to push back on todo
                                }
                                if (adjacentNetworks.addAll(networks)) {
                                    todo.add(adjacent);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public void checkNetwork(World world) {
        for (Map.Entry<Long, ChunkBlob> entry : chunkBlobMap.entrySet()) {
            entry.getValue().check(world);
        }
    }

    private void dump(String prefix, Set<NetworkId> networks) {
        String s = prefix + ": ";
        for (NetworkId network : networks) {
            s += network.getId() + " ";
        }
        System.out.println("s = " + s);
    }


    public void dump() {
        for (ChunkBlob blob : chunkBlobMap.values()) {
            blob.dump();
        }
    }


    public void readFromNBT(NBTTagCompound compound) {
        chunkBlobMap.clear();
        lastNetworkId = compound.getInteger("lastNetwork");
        lastConsumerId = compound.getInteger("lastConsumer");
        if (compound.hasKey("chunks")) {
            NBTTagList chunks = (NBTTagList) compound.getTag("chunks");
            for (int i = 0 ; i < chunks.tagCount() ; i++) {
                NBTTagCompound tc = (NBTTagCompound) chunks.get(i);
                int chunkX = tc.getInteger("chunkX");
                int chunkZ = tc.getInteger("chunkZ");
                ChunkBlob blob = new ChunkBlob(new ChunkPos(chunkX, chunkZ));
                blob.readFromNBT(tc);
                chunkBlobMap.put(blob.getChunkNum(), blob);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("lastNetwork", lastNetworkId);
        compound.setInteger("lastConsumer", lastConsumerId);
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Long, ChunkBlob> entry : chunkBlobMap.entrySet()) {
            ChunkBlob blob = entry.getValue();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("chunkX", blob.getChunkPos().chunkXPos);
            tc.setInteger("chunkZ", blob.getChunkPos().chunkZPos);
            blob.writeToNBT(tc);
            list.appendTag(tc);
        }
        compound.setTag("chunks", list);

        return compound;
    }

}
