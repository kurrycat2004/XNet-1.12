package mcjty.xnet.blocks.controller;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.container.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class ControllerContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_FILTER = 0;
    public static final int COUNT_FILTER_SLOTS = 18;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER),
                    CONTAINER_INVENTORY, SLOT_FILTER, 91, 117, 9, 18, 2, 18);
            layoutPlayerInventorySlots(91, 157);
        }
    };

    public ControllerContainer(EntityPlayer player, IInventory containerInventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, containerInventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
