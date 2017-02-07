package mcjty.xnet.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class WorldBlob {

    private final int dimId;
    private final Map<Long, ChunkBlob> chunkBlobMap = new HashMap<>();

    public WorldBlob(int dimId) {
        this.dimId = dimId;
    }

    public int getDimId() {
        return dimId;
    }

    /**
     * Create a cable segment at a position and return a chunk local blob id.
     */
    public int createCableSegment(BlockPos pos) {
        ChunkPos cpos = new ChunkPos(pos);
        long chunkId = ChunkPos.asLong(cpos.chunkXPos, cpos.chunkZPos);
        if (!chunkBlobMap.containsKey(chunkId)) {
            chunkBlobMap.put(chunkId, new ChunkBlob(chunkId));
        }
        ChunkBlob blob = chunkBlobMap.get(chunkId);
        return blob.createCableSegment(pos);
    }

    public void readFromNBT(NBTTagCompound compound) {
        chunkBlobMap.clear();
        if (compound.hasKey("chunks")) {
            NBTTagList chunks = (NBTTagList) compound.getTag("chunks");
            for (int i = 0 ; i < chunks.tagCount() ; i++) {
                NBTTagCompound tc = (NBTTagCompound) chunks.get(i);
                long chunk = tc.getLong("chunk");
                ChunkBlob blob = new ChunkBlob(chunk);
                blob.readFromNBT(tc);
                chunkBlobMap.put(chunk, blob);
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Long, ChunkBlob> entry : chunkBlobMap.entrySet()) {
            ChunkBlob blob = entry.getValue();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setLong("chunk", blob.getChunkPos());
            blob.writeToNBT(tc);
            list.appendTag(tc);
        }
        compound.setTag("chunks", list);

        return compound;
    }

}
