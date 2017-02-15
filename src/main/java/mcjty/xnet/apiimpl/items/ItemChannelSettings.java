package mcjty.xnet.apiimpl.items;

import mcjty.lib.tools.ItemStackTools;
import mcjty.xnet.api.channels.IChannelSettings;
import mcjty.xnet.api.channels.IConnectorSettings;
import mcjty.xnet.api.channels.IControllerContext;
import mcjty.xnet.api.gui.IEditorGui;
import mcjty.xnet.api.gui.IndicatorIcon;
import mcjty.xnet.api.keys.SidedConsumer;
import mcjty.xnet.blocks.controller.GuiController;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ItemChannelSettings implements IChannelSettings {

    public static final String TAG_MODE = "mode";

    // Cache data
    private Map<SidedConsumer, ItemConnectorSettings> itemExtractors = null;
    private Map<SidedConsumer, ItemConnectorSettings> itemConsumers = null;


    enum ChannelMode {
        PRIORITY,
        ROUNDROBIN,
        RANDOM
    }

    private ChannelMode channelMode = ChannelMode.PRIORITY;

    public ChannelMode getChannelMode() {
        return channelMode;
    }

    public void setChannelMode(ChannelMode channelMode) {
        this.channelMode = channelMode;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        channelMode = ChannelMode.values()[tag.getByte("mode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setByte("mode", (byte) channelMode.ordinal());
    }

    @Override
    public void tick(int channel, IControllerContext context) {
        updateCache(channel, context);
        // @todo optimize
        for (Map.Entry<SidedConsumer, ItemConnectorSettings> entry : itemExtractors.entrySet()) {
            BlockPos consumerPosition = context.findConsumerPosition(entry.getKey().getConsumerId());
            if (consumerPosition != null) {
                EnumFacing side = entry.getKey().getSide();
                BlockPos pos = consumerPosition.offset(side);
                TileEntity te = context.getControllerWorld().getTileEntity(pos);
                IItemHandler handler = getItemHandlerAt(te, side.getOpposite());
                // @todo report error somewhere?
                if (handler != null) {
                    Predicate<ItemStack> extractMatcher = entry.getValue().getMatcher();
                    ItemStack stack = fetchOneItem(handler, true, extractMatcher);
                    if (ItemStackTools.isValid(stack)) {
                        if (insertStack(context, stack, true)) {
                            insertStack(context, fetchOneItem(handler, false, extractMatcher), false);
                        }
                    }
                }
            }
        }
    }

    private boolean insertStack(@Nonnull IControllerContext context, @Nonnull ItemStack stack, boolean simulate) {
        for (Map.Entry<SidedConsumer, ItemConnectorSettings> entry : itemConsumers.entrySet()) {
            ItemConnectorSettings settings = entry.getValue();
            if (settings.getMatcher().test(stack)) {
                BlockPos consumerPosition = context.findConsumerPosition(entry.getKey().getConsumerId());
                if (consumerPosition != null) {
                    EnumFacing side = entry.getKey().getSide();
                    BlockPos pos = consumerPosition.offset(side);
                    TileEntity te = context.getControllerWorld().getTileEntity(pos);
                    IItemHandler handler = getItemHandlerAt(te, side.getOpposite());
                    // @todo report error somewhere?
                    if (handler != null) {
                        if (ItemStackTools.isEmpty(ItemHandlerHelper.insertItem(handler, stack, simulate))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ItemStack fetchOneItem(IItemHandler handler, boolean simulate, Predicate<ItemStack> matcher) {
        for (int i = 0 ; i < handler.getSlots() ; i++) {
            ItemStack stack = handler.extractItem(i, 1, simulate);
            if (ItemStackTools.isValid(stack) && matcher.test(stack)) {
                return stack;
            }
        }
        return ItemStackTools.getEmptyStack();
    }


    private void updateCache(int channel, IControllerContext context) {
        if (itemExtractors == null) {
            itemExtractors = new HashMap<>();
            itemConsumers = new HashMap<>();
            Map<SidedConsumer, IConnectorSettings> connectors = context.getConnectors(channel);
            for (Map.Entry<SidedConsumer, IConnectorSettings> entry : connectors.entrySet()) {
                ItemConnectorSettings con = (ItemConnectorSettings) entry.getValue();
                if (con.getItemMode() == ItemConnectorSettings.ItemMode.EXT) {
                    itemExtractors.put(entry.getKey(), con);
                } else {
                    itemConsumers.put(entry.getKey(), con);
                }
            }
        }
    }

    @Override
    public void cleanCache() {
        itemExtractors = null;
        itemConsumers = null;
    }

    @Override
    public boolean isEnabled(String tag) {
        return true;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return new IndicatorIcon(GuiController.iconGuiElements, 0, 80, 11, 10);
    }

    @Nullable
    @Override
    public String getIndicator() {
        return null;
    }

    @Override
    public void createGui(IEditorGui gui) {
        gui.nl().choices(TAG_MODE, "Item distribution mode", channelMode, ChannelMode.values());
    }

    @Override
    public void update(Map<String, Object> data) {
        channelMode = ChannelMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
    }

    @Nullable
    private IItemHandler getItemHandlerAt(@Nullable TileEntity te, EnumFacing intSide) {
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, intSide)) {
            IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, intSide);
            if (handler != null) {
                return handler;
            }
        } else if (te instanceof ISidedInventory) {
            // Support for old inventory
            ISidedInventory sidedInventory = (ISidedInventory) te;
            return new SidedInvWrapper(sidedInventory, intSide);
        } else if (te instanceof IInventory) {
            // Support for old inventory
            IInventory inventory = (IInventory) te;
            return new InvWrapper(inventory);
        }
        return null;
    }


}
