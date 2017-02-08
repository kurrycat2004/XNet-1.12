package mcjty.xnet.multiblock;

import mcjty.lib.varia.BlockPosTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class ChunkBlob {

    private final long chunkPos;
    private int lastId = 0;             // Local chunk blob ID

    // Every local (chunk) blob id can be allocated to multiple global network id's
    private final Map<BlobId, Set<NetworkId>> idMappings = new HashMap<>();

    // Every position in a chunk can be allocated to one local chunk blob id
    private final Map<IntPos, BlobId> blobAllocations = new HashMap<>();

    // These positions represent network ID providers
    private final Map<IntPos, NetworkId> networkProviders = new HashMap<>();

    public ChunkBlob(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public long getChunkPos() {
        return chunkPos;
    }

    private Set<NetworkId> getMappings(BlobId blobId) {
        if (!idMappings.containsKey(blobId)) {
            idMappings.put(blobId, new HashSet<>());
        }
        return idMappings.get(blobId);
    }

    public void clearNetworkAllocations() {
        idMappings.clear();
    }

    public Map<IntPos, NetworkId> getNetworkProviders() {
        return networkProviders;
    }

    public List<IntPos> createNetworkProvider(BlockPos pos, NetworkId networkId) {
        IntPos posId = new IntPos(pos);
        networkProviders.put(posId, networkId);
        List<IntPos> changed = createCableSegment(pos);
        getMappings(blobAllocations.get(posId)).add(networkId);
        return changed;
    }

    // Create a cable segment and return all positions on the border of this
    // chunk where something changed. Network ids are merged
    public List<IntPos> createCableSegment(BlockPos pos) {
        IntPos posId = new IntPos(pos);
        if (blobAllocations.containsKey(posId)) {
            throw new IllegalArgumentException("There is already a cablesegment at " + BlockPosTools.toString(pos) + "!");
        }

        Set<BlobId> ids = new HashSet<>();
        for (int p : posId.getSidePositions()) {
            if (p != -1) {
                IntPos ip = new IntPos(p);
                if (blobAllocations.containsKey(ip)) {
                    ids.add(blobAllocations.get(ip));
                }
            }
        }

        if (ids.isEmpty()) {
            // New id
            lastId++;
            blobAllocations.put(posId, new BlobId(lastId));
            if (posId.isBorder()) {
                return Collections.singletonList(posId);
            } else {
                return Collections.emptyList();
            }
        } else if (ids.size() == 1) {
            // Merge with existing
            BlobId id = ids.iterator().next();
            blobAllocations.put(posId, id);
            if (posId.isBorder()) {
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
            for (Map.Entry<BlobId, Set<NetworkId>> entry : idMappings.entrySet()) {
                if (ids.contains(entry.getKey())) {
                    networkIds.addAll(entry.getValue());
                }
            }
            idMappings.put(id, networkIds);
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

        int cnt = 0;
        for (int p : posId.getSidePositions()) {
            if (p != -1) {
                if (blobAllocations.containsKey(new IntPos(p))) {
                    cnt++;
                }
            }
        }

        List<IntPos> changed = new ArrayList<>();
        blobAllocations.remove(posId);
        if (posId.isBorder()) {
            changed.add(posId);
        }
        if (cnt > 1) {
            // Multiple adjacent blocks. We might need to split in multiple blobs. For
            // every adjacent block we allocate a new id:
            for (int p : posId.getSidePositions()) {
                if (p != -1) {
                    IntPos ip = new IntPos(p);
                    if (blobAllocations.containsKey(ip)) {
                        BlobId oldId = blobAllocations.get(ip);
                        idMappings.remove(oldId);
                        lastId++;
                        propagateId(ip, oldId, new BlobId(lastId), changed);
                    }
                }
            }
        }
        return changed;
    }

    private void propagateId(IntPos pos, BlobId oldId, BlobId newId, List<IntPos> changed) {
        blobAllocations.put(pos, newId);
        if (pos.isBorder()) {
            changed.add(pos);
        }
        for (int p : pos.getSidePositions()) {
            if (p != -1) {
                IntPos ip = new IntPos(p);
                if (blobAllocations.get(ip).equals(oldId)) {
                    propagateId(ip, oldId, newId, changed);
                }
            }
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        idMappings.clear();
        blobAllocations.clear();
        networkProviders.clear();

        lastId = compound.getInteger("lastId");
        if (compound.hasKey("mappings")) {
            int[] mappings = compound.getIntArray("mappings");
            int idx = 0;
            while (idx < mappings.length-1) {
                int key = mappings[idx];
                BlobId id = new BlobId(key);
                Set<NetworkId> ids = new HashSet<>();
                idMappings.put(id, ids);
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
                blobAllocations.put(new IntPos(allocations[idx]), new BlobId(allocations[idx+1]));
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
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("lastId", lastId);

        List<Integer> m = new ArrayList<>();
        for (Map.Entry<BlobId, Set<NetworkId>> entry : idMappings.entrySet()) {
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

        return compound;
    }

}
