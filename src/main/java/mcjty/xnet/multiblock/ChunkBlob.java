package mcjty.xnet.multiblock;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import mcjty.lib.varia.BlockPosTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class ChunkBlob {

    private final long chunkPos;
    private int lastId = 0;             // Local chunk blob ID

    // Every local (chunk) blob id can be allocated to multiple global network id's
    private final Map<Integer, Set<Integer>> idMappings = new HashMap<>();

    // Every position in a chunk can be allocated to one local chunk blob id
    // First Integer is a local chunk blockpos coordinate converted to int
    // Second is bloc ID
    private final Map<Integer, Integer> blobAllocations = new HashMap<>();

    // These positions represent network ID providers
    // First is location, second is network Id
    private final Map<Integer, Integer> networkIdProviders = new HashMap<>();

    public ChunkBlob(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public long getChunkPos() {
        return chunkPos;
    }

    private Set<Integer> getMappings(int blobId) {
        if (!idMappings.containsKey(blobId)) {
            idMappings.put(blobId, new HashSet<>());
        }
        return idMappings.get(blobId);
    }

    public List<Integer> createNetworkProvider(BlockPos pos, int networkId) {
        int posId = IntPosTools.posToInt(pos);
        networkIdProviders.put(posId, networkId);
        List<Integer> changed = createCableSegment(pos);
        getMappings(blobAllocations.get(posId)).add(networkId);
        return changed;
    }

    // Create a cable segment and return all positions on the border of this
    // chunk where something changed. Network ids are merged
    public List<Integer> createCableSegment(BlockPos pos) {
        int posId = IntPosTools.posToInt(pos);
        if (blobAllocations.containsKey(posId)) {
            throw new IllegalArgumentException("There is already a cablesegment at " + BlockPosTools.toString(pos) + "!");
        }

        TIntSet ids = new TIntHashSet();
        for (int p : IntPosTools.getSidePositions(posId)) {
            if (blobAllocations.containsKey(p)) {
                ids.add(blobAllocations.get(p));
            }
        }

        if (ids.isEmpty()) {
            // New id
            lastId++;
            blobAllocations.put(posId, lastId);
            if (IntPosTools.isBorder(posId)) {
                return Collections.singletonList(posId);
            } else {
                return Collections.emptyList();
            }
        } else if (ids.size() == 1) {
            // Merge with existing
            int id = ids.iterator().next();
            blobAllocations.put(posId, id);
            if (IntPosTools.isBorder(posId)) {
                return Collections.singletonList(posId);
            } else {
                return Collections.emptyList();
            }
        } else {
            // Merge several blobs
            List<Integer> changed = new ArrayList<>();
            int id = ids.iterator().next();
            blobAllocations.put(posId, id);
            if (IntPosTools.isBorder(posId)) {
                changed.add(posId);
            }
            for (Map.Entry<Integer, Integer> entry : blobAllocations.entrySet()) {
                if (ids.contains(entry.getValue())) {
                    Integer p = entry.getKey();
                    blobAllocations.put(p, id);
                    if (IntPosTools.isBorder(p)) {
                        changed.add(p);
                    }
                }
            }
            Set<Integer> networkIds = new HashSet<>();
            for (Map.Entry<Integer, Set<Integer>> entry : idMappings.entrySet()) {
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
    public List<Integer> removeCableSegment(BlockPos pos) {
        int posId = IntPosTools.posToInt(pos);
        if (!blobAllocations.containsKey(posId)) {
            throw new IllegalArgumentException("There is no cablesegment at " + BlockPosTools.toString(pos) + "!");
        }
        networkIdProviders.remove(posId);

        int cnt = 0;
        for (int p : IntPosTools.getSidePositions(posId)) {
            if (blobAllocations.containsKey(p)) {
                cnt++;
            }
        }

        List<Integer> changed = new ArrayList<>();
        blobAllocations.remove(posId);
        if (IntPosTools.isBorder(posId)) {
            changed.add(posId);
        }
        if (cnt > 1) {
            // Multiple adjacent blocks. We might need to split in multiple blobs. For
            // every adjacent block we allocate a new id:
            for (int p : IntPosTools.getSidePositions(posId)) {
                if (blobAllocations.containsKey(p)) {
                    int oldId = blobAllocations.get(p);
                    idMappings.remove(oldId);
                    lastId++;
                    propagateId(p, oldId, lastId, changed);
                }
            }
        }
        return changed;
    }

    private void propagateId(int pos, int oldId, int newId, List<Integer> changed) {
        blobAllocations.put(pos, newId);
        if (IntPosTools.isBorder(pos)) {
            changed.add(pos);
        }
        for (int p : IntPosTools.getSidePositions(pos)) {
            if (blobAllocations.get(p) == oldId) {
                propagateId(p, oldId, newId, changed);
            }
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        idMappings.clear();
        blobAllocations.clear();

        lastId = compound.getInteger("lastId");
        if (compound.hasKey("mappings")) {
            int[] mappings = compound.getIntArray("mappings");
            int idx = 0;
            while (idx < mappings.length-1) {
                Integer key = mappings[idx];
                Set<Integer> ids = new HashSet<>();
                idMappings.put(key, ids);
                idx++;
                while (idx < mappings.length && mappings[idx] != -1) {
                    ids.add(mappings[idx]);
                    idx++;
                }
                idx++;
            }
        }

        if (compound.hasKey("allocations")) {
            int[] allocations = compound.getIntArray("allocations");
            int idx = 0;
            while (idx < allocations.length-1) {
                blobAllocations.put(allocations[idx], allocations[idx]+1);
                idx += 2;
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("lastId", lastId);

        List<Integer> m = new ArrayList<>();
        for (Map.Entry<Integer, Set<Integer>> entry : idMappings.entrySet()) {
            m.add(entry.getKey());
            for (Integer v : entry.getValue()) {
                m.add(v);
            }
            m.add(-1);
        }
        NBTTagIntArray mappings = new NBTTagIntArray(m.stream().mapToInt(i -> i).toArray());
        compound.setTag("mappings", mappings);

        m.clear();
        for (Map.Entry<Integer, Integer> entry : blobAllocations.entrySet()) {
            m.add(entry.getKey());
            m.add(entry.getValue());
        }
        NBTTagIntArray allocations = new NBTTagIntArray(m.stream().mapToInt(i -> i).toArray());
        compound.setTag("allocations", allocations);

        return compound;
    }

}
