package mcjty.xnet.multiblock;

import mcjty.lib.varia.GlobalCoordinate;
import mcjty.lib.worlddata.AbstractWorldData;
import mcjty.xnet.api.keys.NetworkId;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class XNetWirelessChannels extends AbstractWorldData<XNetWirelessChannels> {

    private static final String NAME = "XNetWirelessChannels";

    private final Map<String, WirelessChannelInfo> channelToWireless = new HashMap<>();

    public XNetWirelessChannels(String name) {
        super(name);
    }

    @Override
    public void clear() {
        channelToWireless.clear();
    }

    public void publishChannel(String channel, int dimension, BlockPos wirelessRouterPos, NetworkId network) {
        WirelessChannelInfo info;
        if (channelToWireless.containsKey(channel)) {
            info = channelToWireless.get(channel);
        } else {
            info = new WirelessChannelInfo(new GlobalCoordinate(wirelessRouterPos, dimension));
            channelToWireless.put(channel, info);
        }
        info.setAge(0);
        info.setNetworkId(network);
        save();
    }

    public void tick(World world, int amount) {
        if (channelToWireless.isEmpty()) {
            return;
        }

        Set<String> toDelete = new HashSet<>();
        for (Map.Entry<String, WirelessChannelInfo> entry : channelToWireless.entrySet()) {
            WirelessChannelInfo info = entry.getValue();
            int age = info.getAge();
            age += amount;
            if (age > 40) { // @todo configurable
                toDelete.add(entry.getKey());
            }
        }

        if (!toDelete.isEmpty()) {
            XNetBlobData blobData = XNetBlobData.getBlobData(world);
            WorldBlob worldBlob = blobData.getWorldBlob(world);
            for (String key : toDelete) {
                worldBlob.incNetworkVersion(channelToWireless.get(key).getNetworkId());
                channelToWireless.remove(key);
            }
        }

        save();
    }

    @Nonnull
    public static XNetWirelessChannels getWirelessChannels(World world) {
        return getData(world, XNetWirelessChannels.class, NAME);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        channelToWireless.clear();
        NBTTagList tagList = compound.getTagList("channels", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < tagList.tagCount() ; i++) {
            NBTTagCompound tc = tagList.getCompoundTagAt(i);
            WirelessChannelInfo info = new WirelessChannelInfo(new GlobalCoordinate(
                    new BlockPos(tc.getInteger("x"), tc.getInteger("y"), tc.getInteger("z")),
                    tc.getInteger("dim")));
            info.setAge(tc.getInteger("age"));
            info.setNetworkId(new NetworkId(tc.getInteger("network")));
            channelToWireless.put(tc.getString("name"), info);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();

        for (Map.Entry<String, WirelessChannelInfo> entry : channelToWireless.entrySet()) {
            NBTTagCompound tc = new NBTTagCompound();
            WirelessChannelInfo info = entry.getValue();
            tc.setString("name", entry.getKey());
            tc.setInteger("dim", info.getWirelessRouterPos().getDimension());
            tc.setInteger("x", info.getWirelessRouterPos().getCoordinate().getX());
            tc.setInteger("y", info.getWirelessRouterPos().getCoordinate().getY());
            tc.setInteger("z", info.getWirelessRouterPos().getCoordinate().getZ());
            tc.setInteger("age", info.getAge());
            tc.setInteger("network", info.getNetworkId().getId());
            tagList.appendTag(tc);
        }

        compound.setTag("channels", tagList);

        return compound;
    }

    public static class WirelessChannelInfo {
        private final GlobalCoordinate wirelessRouterPos;
        private int age;
        private NetworkId networkId;

        public WirelessChannelInfo(GlobalCoordinate wirelessRouterPos) {
            this.wirelessRouterPos = wirelessRouterPos;
            age = 0;
        }

        public GlobalCoordinate getWirelessRouterPos() {
            return wirelessRouterPos;
        }

        public NetworkId getNetworkId() {
            return networkId;
        }

        public void setNetworkId(NetworkId networkId) {
            this.networkId = networkId;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
