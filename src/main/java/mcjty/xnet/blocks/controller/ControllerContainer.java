package mcjty.xnet.blocks.controller;

import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class ControllerContainer extends GenericContainer {
    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            layoutPlayerInventorySlots(91, 157);
        }
    };

    public ControllerContainer(EntityPlayer player, IInventory inventory) {
        super(factory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }
}
