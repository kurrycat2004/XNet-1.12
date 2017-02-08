package mcjty.xnet.multiblock;

import mcjty.lib.varia.BlockPosTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

/**
 * All blobs in a single chunk are represented here as wel
 * as how blob id's are mapped to global network id's.
 */
public class ChunkBlob {

    private final long chunkPos;
    private int lastId = 0;             // Local chunk blob ID

    // Every local (chunk) blob id can be allocated to multiple global network id's
    private final Map<BlobId, Set<NetworkId>> networkMappings = new HashMap<>();

    // Every position in a chunk can be allocated to one local chunk blob id
    private final Map<IntPos, BlobId> blobAllocations = new HashMap<>();

    // These positions represent network ID providers
    private final Map<IntPos, NetworkId> networkProviders = new HashMap<>();

    // Blob id are mapped to colors
    private final Map<BlobId, ColorId> blobColors = new HashMap<>();

    // Transient datastructure that caches are positions at the border
    private final Set<IntPos> borderPositions = new HashSet<>();

    public ChunkBlob(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public long getChunkPos() {
        return chunkPos;
    }

    public BlockPos getPosition(IntPos pos) {
        ChunkPos chunkPos = IntPos.chunkPosFromLong(this.chunkPos);
        return pos.toBlockPos(chunkPos);
    }

    public Set<NetworkId> getNetworksForPosition(IntPos pos) {
        return networkMappings.get(blobAllocations.get(pos));
    }

    public Set<IntPos> getBorderPositions() {
        return borderPositions;
    }

    private Set<NetworkId> getMappings(BlobId blobId) {
        if (!networkMappings.containsKey(blobId)) {
            networkMappings.put(blobId, new HashSet<>());
        }
        return networkMappings.get(blobId);
    }

    // Go over all network providers in this chunk and distribute their id's
    // to the local blob id's
    public void fixNetworkAllocations() {
        networkMappings.clear();
        for (Map.Entry<IntPos, NetworkId> entry : networkProviders.entrySet()) {
            BlobId blobId = blobAllocations.get(entry.getKey());
            getMappings(blobId).add(entry.getValue());
        }
    }

    public Map<IntPos, NetworkId> getNetworkProviders() {
        return networkProviders;
    }

    public List<IntPos> createNetworkProvider(BlockPos pos, ColorId color, NetworkId networkId) {
        IntPos posId = new IntPos(pos);
        networkProviders.put(posId, networkId);
        List<IntPos> changed = createCableSegment(pos, color);
        getMappings(blobAllocations.get(posId)).add(networkId);
        return changed;
    }

    // Create a cable segment and return all positions on the border of this
    // chunk where something changed. Network ids are merged
    public List<IntPos> createCableSegment(BlockPos pos, ColorId color) {
        IntPos posId = new IntPos(pos);
        if (blobAllocations.containsKey(posId)) {
            throw new IllegalArgumentException("There is already a cablesegment at " + BlockPosTools.toString(pos) + "!");
        }

        Set<BlobId> ids = new HashSet<>();
        for (int p : posId.getSidePositions()) {
            if (p != -1) {
                IntPos ip = new IntPos(p);
                BlobId blobId = blobAllocations.get(ip);
                if (blobId != null) {
                    if (blobColors.get(blobId).equals(color)) {
                        ids.add(blobId);
                    }
                }
            }
        }

        if (ids.isEmpty()) {
            // New id
            lastId++;
            BlobId blobId = new BlobId(lastId);
            blobAllocations.put(posId, blobId);
            blobColors.put(blobId, color);
            if (posId.isBorder()) {
                borderPositions.add(posId);
                return Collections.singletonList(posId);
            } else {
                return Collections.emptyList();
            }
        } else if (ids.size() == 1) {
            // Merge with existing
            BlobId id = ids.iterator().next();
            blobAllocations.put(posId, id);
            if (posId.isBorder()) {
                borderPositions.add(posId);
                return Collections.singletonList(posId);
            } else {
                return Collections.emptyList();
            }
        } else {
            // Merge several blobs
            List<IntPos> changed = new ArrayList<>();
            BlobId id = ids.iterator().next();
            blobAllocations.put(posId, id);
            if (posId.isBorder()) {
                borderPositions.add(posId);
                changed.add(posId);
            }
            for (Map.Entry<IntPos, BlobId> entry : blobAllocations.entrySet()) {
                if (ids.contains(entry.getValue())) {
                    IntPos p = entry.getKey();
                    blobAllocations.put(p, id);
                    if (p.isBorder()) {
                        changed.add(p);
                    }
                }
            }
            Set<NetworkId> networkIds = new HashSet<>();
            for (Map.Entry<BlobId, Set<NetworkId>> entry : networkMappings.entrySet()) {
                if (ids.contains(entry.getKey())) {
                    networkIds.addAll(entry.getValue());
                }
            }
            networkMappings.put(id, networkIds);
            return changed;
        }
    }

    // Remove a cable segment and return all positions on the border of this
    // chunk where something changed. Note that this function will unlink all
    // affected network Ids (except from providers) so you have to make sure
    // to traverse the network providers again
    public List<IntPos> removeCableSegment(BlockPos pos) {
        IntPos posId = new IntPos(pos);
        if (!blobAllocations.containsKey(posId)) {
            throw new IllegalArgumentException("There is no cablesegment at " + BlockPosTools.toString(pos) + "!");
        }
        networkProviders.remove(posId);
        ColorId oldColor = blobColors.get(blobAllocations.get(posId));
        // @todo: old blob ids are never cleaned up with regards to color allocation

        int cnt = 0;
        for (int p : posId.getSidePositions()) {
            if (p != -1) {
                BlobId blobId = blobAllocations.get(new IntPos(p));
                if (blobId != null && blobColors.get(blobId).equals(oldColor)) {
                    cnt++;
                }
            }
        }

        List<IntPos> changed = new ArrayList<>();
        blobAllocations.remove(posId);
        if (posId.isBorder()) {
            borderPositions.remove(posId);
            changed.add(posId);
        }
        if (cnt > 1) {
            // Multiple adjacent blocks. We might need to split in multiple blobs. For
            // every adjacent block we allocate a new id:
            for (int p : posId.getSidePositions()) {
                if (p != -1) {
                    IntPos ip = new IntPos(p);
                    BlobId oldId = blobAllocations.get(ip);
                    if (oldId != null && blobColors.get(oldId).equals(oldColor)) {
                        networkMappings.remove(oldId);
                        lastId++;
                        propagateId(ip, oldColor, oldId, new BlobId(lastId), changed);
                    }
                }
            }
        }
        return changed;
    }

    private void propagateId(IntPos pos, ColorId color, BlobId oldId, BlobId newId, List<IntPos> changed) {
        blobAllocations.put(pos, newId);
        if (pos.isBorder()) {
            changed.add(pos);
        }
        for (int p : pos.getSidePositions()) {
            if (p != -1) {
                IntPos ip = new IntPos(p);
                BlobId blobId = blobAllocations.get(ip);
                if (blobId.equals(oldId) && blobColors.get(blobId).equals(color)) {
                    propagateId(ip, color, oldId, newId, changed);
                }
            }
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        networkMappings.clear();
        blobAllocations.clear();
        networkProviders.clear();
        blobColors.clear();
        borderPositions.clear();

        lastId = compound.getInteger("lastId");
        if (compound.hasKey("mappings")) {
            int[] mappings = compound.getIntArray("mappings");
            int idx = 0;
            while (idx < mappings.length-1) {
                int key = mappings[idx];
                BlobId id = new BlobId(key);
                Set<NetworkId> ids = new HashSet<>();
                networkMappings.put(id, ids);
                idx++;
                while (idx < mappings.length && mappings[idx] != -1) {
                    ids.add(new NetworkId(mappings[idx]));
                    idx++;
                }
                idx++;
            }
        }

        if (compound.hasKey("allocations")) {
            int[] allocations = compound.getIntArray("allocations");
            int idx = 0;
            while (idx < allocations.length-1) {
                IntPos pos = new IntPos(allocations[idx]);
                blobAllocations.put(pos, new BlobId(allocations[idx+1]));
                if (pos.isBorder()) {
                    borderPositions.add(pos);
                }
                idx += 2;
            }
        }

        if (compound.hasKey("providers")) {
            int[] providers = compound.getIntArray("providers");
            int idx = 0;
            while (idx < providers.length-1) {
                networkProviders.put(new IntPos(providers[idx]), new NetworkId(providers[idx+1]));
                idx += 2;
            }
        }

        if (compound.hasKey("colors")) {
            int[] colors = compound.getIntArray("colors");
            int idx = 0;
            while (idx < colors.length-1) {
                blobColors.put(new BlobId(colors[idx]), new ColorId(colors[idx+1]));
                idx += 2;
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("lastId", lastId);

        List<Integer> m = new ArrayList<>();
        for (Map.Entry<BlobId, Set<NetworkId>> entry : networkMappings.entrySet()) {
            m.add(entry.getKey().getId());
            for (NetworkId v : entry.getValue()) {
                m.add(v.getId());
            }
            m.add(-1);
        }
        NBTTagIntArray mappings = new NBTTagIntArray(m.stream().mapToInt(i -> i).toArray());
        compound.setTag("mappings", mappings);

        m.clear();
        for (Map.Entry<IntPos, BlobId> entry : blobAllocations.entrySet()) {
            m.add(entry.getKey().getPos());
            m.add(entry.getValue().getId());
        }
        NBTTagIntArray allocations = new NBTTagIntArray(m.stream().mapToInt(i -> i).toArray());
        compound.setTag("allocations", allocations);

        m.clear();
        for (Map.Entry<IntPos, NetworkId> entry : networkProviders.entrySet()) {
            m.add(entry.getKey().getPos());
            m.add(entry.getValue().getId());
        }
        NBTTagIntArray providers = new NBTTagIntArray(m.stream().mapToInt(i -> i).toArray());
        compound.setTag("providers", providers);

        m.clear();
        for (Map.Entry<BlobId, ColorId> entry : blobColors.entrySet()) {
            m.add(entry.getKey().getId());
            m.add(entry.getValue().getId());
        }
        NBTTagIntArray colors = new NBTTagIntArray(m.stream().mapToInt(i -> i).toArray());
        compound.setTag("colors", colors);

        return compound;
    }

}
