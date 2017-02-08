package mcjty.xnet.multiblock;

import mcjty.lib.tools.WorldTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.HashMap;
import java.util.Map;

public class XNetBlobData extends WorldSavedData {

    public static final String NAME = "XNetBlobData";
    private static XNetBlobData instance = null;

    private final Map<Integer, WorldBlob> worldBlobMap = new HashMap<>();

    public XNetBlobData(String identifier) {
        super(identifier);
    }

    public void save(World world) {
        WorldTools.saveData(world, NAME, this);
        markDirty();
    }

    public static void clearInstance() {
        if (instance != null) {
            instance.worldBlobMap.clear();
            instance = null;
        }
    }

    public static XNetBlobData getBlobData(World world) {
        if (world.isRemote) {
            return null;
        }
        if (instance != null) {
            return instance;
        }
        instance = mcjty.lib.tools.WorldTools.loadData(world, XNetBlobData.class, NAME);
        if (instance == null) {
            instance = new XNetBlobData(NAME);
        }
        return instance;
    }

    public WorldBlob getWorldBlob(World world) {
        int dimId = world.provider.getDimension();
        if (!worldBlobMap.containsKey(dimId)) {
            worldBlobMap.put(dimId, new WorldBlob(dimId));
        }
        return worldBlobMap.get(dimId);
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        worldBlobMap.clear();
        if (compound.hasKey("worlds")) {
            NBTTagList worlds = (NBTTagList) compound.getTag("worlds");
            for (int i = 0 ; i < worlds.tagCount() ; i++) {
                NBTTagCompound tc = (NBTTagCompound) worlds.get(i);
                int id = tc.getInteger("dimid");
                WorldBlob blob = new WorldBlob(id);
                blob.readFromNBT(tc);
                worldBlobMap.put(id, blob);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, WorldBlob> entry : worldBlobMap.entrySet()) {
            WorldBlob blob = entry.getValue();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("dimid", blob.getDimId());
            blob.writeToNBT(tc);
            list.appendTag(tc);
        }
        compound.setTag("worlds", list);

        return compound;
    }
}
