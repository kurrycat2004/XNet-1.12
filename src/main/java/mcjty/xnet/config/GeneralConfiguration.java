package mcjty.xnet.config;

import mcjty.xnet.blocks.wireless.TileEntityWirelessRouter;
import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {
    public static final String CATEGORY_GENERAL = "general";

    public static int controllerMaxRF = 100000;
    public static int controllerRfPerTick = 1000;

    public static int wirelessRouterMaxRF = 100000;
    public static int wirelessRouterRfPerTick = 5000;
    public static int wirelessRouterRfPerChannel[] = new int[] { 20, 50, 200 };

    public static int maxRfConnector = 50000;
    public static int maxRfAdvancedConnector = 500000;

    public static int maxRfRateNormal = 10000;
    public static int maxRfRateAdvanced = 100000;

    public static int maxFluidRateNormal = 1000;
    public static int maxFluidRateAdvanced = 5000;

    public static int controllerRFT = 0;          // RF per tick that the controller uses all the time
    public static int controllerChannelRFT = 1;   // RF Per tick per enabled channel
    public static int controllerOperationRFT = 2; // RF Per tick per operation

    public static int maxPublishedChannels = 32;    // Maximum number of published channels on a routing network

    public static int antennaTier1Range = 100;
    public static int antennaTier2Range = 500;

    public static boolean showNonFacadedCablesWhileSneaking = true;

    public static String[] unsidedBlocks = new String[] {
            "minecraft:chest",
            "minecraft:trapped_chest",
            "rftools:modular_storage",
            "rftools:storage_scanner",
            "rftools:pearl_injector",
    };

    public static void init(Configuration cfg) {

        unsidedBlocks = cfg.getStringList("unsidedBlocks", CATEGORY_GENERAL, unsidedBlocks, "This is a list of blocks that XNet considers to be 'unsided' meaning that it doesn't matter from what side you access things. This is currently only used to help with pasting channels");

        controllerMaxRF = cfg.getInt("controllerMaxRF", CATEGORY_GENERAL, controllerMaxRF, 1, 1000000000,
                "Maximum RF the controller can store");
        controllerRfPerTick = cfg.getInt("controllerRfPerTick", CATEGORY_GENERAL, controllerRfPerTick, 1, 1000000000,
                "Maximum RF the controller can receive per tick");
        wirelessRouterMaxRF = cfg.getInt("wirelessRouterMaxRF", CATEGORY_GENERAL, wirelessRouterMaxRF, 1, 1000000000,
                "Maximum RF the wireless router can store");
        wirelessRouterRfPerTick = cfg.getInt("wirelessRouterRfPerTick", CATEGORY_GENERAL, wirelessRouterRfPerTick, 1, 1000000000,
                "Maximum RF the wireless router can receive per tick");
        wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_1] = cfg.getInt("wireless1RfPerChannel", CATEGORY_GENERAL, wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_1], 0, 1000000000,
                "Maximum RF per tick the wireless router (tier 1) needs to publish a channel");
        wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_2] = cfg.getInt("wireless2RfPerChannel", CATEGORY_GENERAL, wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_2], 0, 1000000000,
                "Maximum RF per tick the wireless router (tier 2) needs to publish a channel");
        wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_INF] = cfg.getInt("wirelessInfRfPerChannel", CATEGORY_GENERAL, wirelessRouterRfPerChannel[TileEntityWirelessRouter.TIER_INF], 0, 1000000000,
                "Maximum RF per tick the wireless router (infinite tier) needs to publish a channel");

        maxRfConnector = cfg.getInt("maxRfConnector", CATEGORY_GENERAL, maxRfConnector, 1, 1000000000,
                "Maximum RF the normal connector can store");
        maxRfAdvancedConnector = cfg.getInt("maxRfAdvancedConnector", CATEGORY_GENERAL, maxRfAdvancedConnector, 1, 1000000000,
                "Maximum RF the advanced connector can store");
        maxRfRateNormal = cfg.getInt("maxRfRateNormal", CATEGORY_GENERAL, maxRfRateNormal, 1, 1000000000,
                "Maximum RF/rate that a normal connector can input or output");
        maxRfRateAdvanced = cfg.getInt("maxRfRateAdvanced", CATEGORY_GENERAL, maxRfRateAdvanced, 1, 1000000000,
                "Maximum RF/rate that an advanced connector can input or output");
        maxFluidRateNormal = cfg.getInt("maxFluidRateNormal", CATEGORY_GENERAL, maxFluidRateNormal, 1, 1000000000,
                "Maximum fluid per operation that a normal connector can input or output");
        maxFluidRateAdvanced = cfg.getInt("maxFluidRateAdvanced", CATEGORY_GENERAL, maxFluidRateAdvanced, 1, 1000000000,
                "Maximum fluid per operation that an advanced connector can input or output");

        maxPublishedChannels = cfg.getInt("maxPublishedChannels", CATEGORY_GENERAL, maxPublishedChannels, 1, 1000000000,
                "Maximum number of published channels that a routing channel can support");

        controllerRFT = cfg.getInt("controllerRFPerTick", CATEGORY_GENERAL, controllerRFT, 0, 1000000000,
                "Power usage for the controller regardless of what it is doing");
        controllerChannelRFT = cfg.getInt("controllerChannelRFT", CATEGORY_GENERAL, controllerChannelRFT, 0, 1000000000,
                "Power usage for the controller per active channel");
        controllerOperationRFT = cfg.getInt("controllerOperationRFT", CATEGORY_GENERAL, controllerOperationRFT, 0, 1000000000,
                "Power usage for the controller per operation performed by one of the channels");
        showNonFacadedCablesWhileSneaking = cfg.getBoolean("showNonFacadedCablesWhileSneaking", CATEGORY_GENERAL, showNonFacadedCablesWhileSneaking,
                "If true then cables are also shown when sneaking even if they are not in a facade");

        antennaTier1Range = cfg.getInt("antennaTier1Range", CATEGORY_GENERAL, antennaTier1Range, 0, 1000000000,
                "Range for a tier 1 antenna");
        antennaTier2Range = cfg.getInt("antennaTier2Range", CATEGORY_GENERAL, antennaTier2Range, 0, 1000000000,
                "Range for a tier 2 antenna");

    }
}
