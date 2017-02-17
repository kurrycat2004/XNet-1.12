package mcjty.xnet.commands;

import mcjty.lib.compat.CompatCommand;
import mcjty.lib.compat.CompatCommandBase;
import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.blocks.cables.NetCableSetup;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import mcjty.rftools.blocks.teleporter.TeleportDestination;
//import mcjty.rftools.blocks.teleporter.TeleportationTools;

public class CommandGen implements CompatCommand {

    @Override
    public String getName() {
        return "xnetgen";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return getName();
    }


    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        XNetBlobData data = XNetBlobData.getBlobData(server.getEntityWorld());
        EntityPlayer player = (EntityPlayer) sender;

        EnumFacing facing = player.getHorizontalFacing();
        BlockPos pos = player.getPosition();
        System.out.println("facing = " + facing);
        System.out.println("pos = " + pos);
        World world = player.getEntityWorld();
        for (int i = 0 ; i < 1000 ; i++) {
            System.out.println("i = " + i);
            world.setBlockState(pos, NetCableSetup.netCableBlock.getDefaultState());
            world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
            NetCableSetup.netCableBlock.createCableSegment(world, pos, ItemStackTools.getEmptyStack());
            pos = pos.offset(facing);
        }

//        TeleportationTools.performTeleport(player, new TeleportDestination(pos, 0), 0, 0, true);
        System.out.println("done");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(String[] sender, int p_82358_2_) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(CompatCommandBase.getCommandName(o));
    }
}
