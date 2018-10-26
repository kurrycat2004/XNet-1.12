package mcjty.xnet.blocks.controller;

import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class KnownUnsidedBlocks {

    private static final Set<ResourceLocation> UNSIDED_BLOCKS = new HashSet<>();

    public static boolean isUnsided(ResourceLocation resourceLocation) {
        if (UNSIDED_BLOCKS.isEmpty()) {
            UNSIDED_BLOCKS.add(Blocks.CHEST.getRegistryName());
            UNSIDED_BLOCKS.add(Blocks.TRAPPED_CHEST.getRegistryName());
            UNSIDED_BLOCKS.add(new ResourceLocation("rftools", "modular_storage"));
            UNSIDED_BLOCKS.add(new ResourceLocation("rftools", "pearl_injector"));
            UNSIDED_BLOCKS.add(new ResourceLocation("rftools", "storage_scanner"));
        }
        return UNSIDED_BLOCKS.contains(resourceLocation);
    }

}
