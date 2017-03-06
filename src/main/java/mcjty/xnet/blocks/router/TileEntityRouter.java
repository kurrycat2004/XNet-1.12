package mcjty.xnet.blocks.router;

import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.typed.Type;
import mcjty.xnet.logic.ControllerChannelClientInfo;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class TileEntityRouter extends GenericTileEntity {

    public static final String CMD_GETCHANNELS = "getChannelInfo";
    public static final String CLIENTCMD_CHANNELSREADY = "channelsReady";

    public TileEntityRouter() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
    }

    @Nonnull
    private List<ControllerChannelClientInfo> findChannelInfo() {
        List<ControllerChannelClientInfo> list = new ArrayList<>();
        // @todo
        return list;
    }


    @Nonnull
    @Override
    public <T> List<T> executeWithResultList(String command, Map<String, Argument> args, Type<T> type) {
        List<T> rc = super.executeWithResultList(command, args, type);
        if (!rc.isEmpty()) {
            return rc;
        }
        if (CMD_GETCHANNELS.equals(command)) {
            return type.convert(findChannelInfo());
        }
        return Collections.emptyList();
    }

    @Override
    public <T> boolean execute(String command, List<T> list, Type<T> type) {
        boolean rc = super.execute(command, list, type);
        if (rc) {
            return true;
        }
        if (CLIENTCMD_CHANNELSREADY.equals(command)) {
            GuiRouter.fromServer_channels = new ArrayList<>(Type.create(ControllerChannelClientInfo.class).convert(list));
            return true;
        }
        return false;
    }
}
