package mcjty.xnet.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;

import java.util.*;

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
     * Create a cable segment at a position
     */
    public void createCableSegment(BlockPos pos, ColorId color) {
        ChunkPos cpos = new ChunkPos(pos);
        long chunkId = ChunkPos.asLong(cpos.chunkXPos, cpos.chunkZPos);
        if (!chunkBlobMap.containsKey(chunkId)) {
            chunkBlobMap.put(chunkId, new ChunkBlob(chunkId));
        }
        ChunkBlob blob = chunkBlobMap.get(chunkId);
        List<IntPos> changed = blob.createCableSegment(pos, color);
        //@todo
    }

    public void recalculateNetwork() {
        // First make sure that every chunk has its network mappings correct (mapping
        // from blob id to network id)
        for (ChunkBlob blob : chunkBlobMap.values()) {
            blob.fixNetworkAllocations();
        }

        // For every chunk we check all border positions and see where they connect with
        // adjacent chunks
        Set<ChunkBlob> todo = new HashSet<>(chunkBlobMap.values());
        while (!todo.isEmpty()) {
            ChunkBlob blob = todo.iterator().next();
            todo.remove(blob);

            Set<IntPos> borderPositions = blob.getBorderPositions();
            ChunkPos chunkPos = IntPos.chunkPosFromLong(blob.getChunkPos());
            for (IntPos pos : borderPositions) {
                Set<NetworkId> networks = blob.getNetworksForPosition(pos);
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (pos.isBorder(facing)) {
                        Vec3i vec = facing.getDirectionVec();
                        ChunkBlob adjacent = chunkBlobMap.get(
                                ChunkPos.asLong(chunkPos.chunkXPos+vec.getX(), chunkPos.chunkZPos+vec.getZ()));
                        if (adjacent != null) {
                            IntPos connectedPos = pos.otherSide(facing);
                            if (adjacent.getBorderPositions().contains(connectedPos)) {
                                // We have a connection!
                                Set<NetworkId> adjacentNetworks = adjacent.getNetworksForPosition(connectedPos);
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


        // @todo
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
