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

    // Every block in a chunk can be allocated to one local chunk blob id
    // (the first Integer is a local chunk blockpos coordinate converted to int)
    private final Map<Integer, Integer> blobAllocations = new HashMap<>();

    public ChunkBlob(long chunkPos) {
        this.chunkPos = chunkPos;
    }

    public long getChunkPos() {
        return chunkPos;
    }

    public int createCableSegment(BlockPos pos) {
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
            return lastId;
        } else if (ids.size() == 1) {
            // Merge with existing
            int id = ids.iterator().next();
            blobAllocations.put(posId, id);
            return id;
        } else {
            // Merge several blobs
            int id = ids.iterator().next();
            blobAllocations.put(posId, id);
            for (Map.Entry<Integer, Integer> entry : blobAllocations.entrySet()) {
                if (ids.contains(entry.getValue())) {
                    blobAllocations.put(entry.getKey(), id);
                }
            }
            Set<Integer> networkIds = new HashSet<>();
            for (Map.Entry<Integer, Set<Integer>> entry : idMappings.entrySet()) {
                if (ids.contains(entry.getKey())) {
                    networkIds.addAll(entry.getValue());
                }
            }
            idMappings.put(id, networkIds);
            return id;
        }
    }

    public void removeCableSegment(BlockPos pos) {
        int posId = IntPosTools.posToInt(pos);
        if (!blobAllocations.containsKey(posId)) {
            throw new IllegalArgumentException("There is no cablesegment at " + BlockPosTools.toString(pos) + "!");
        }

        // @todo
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
