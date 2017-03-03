package mcjty.xnet.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {
    public static final String CATEGORY_GENERAL = "general";

    public static int maxRfConnector = 50000;
    public static int maxRfAdvancedConnector = 500000;

    public static int controllerRFT = 0;          // RF per tick that the controller uses all the time
    public static int controllerChannelRFT = 1;   // RF Per tick per enabled channel
    public static int controllerOperationRFT = 2; // RF Per tick per operation

    public static void init(Configuration cfg) {

        maxRfConnector = cfg.getInt(CATEGORY_GENERAL, "maxRfConnector", maxRfConnector, 1, 1000000000,
                "Maximum RF the normal connector can store");
        maxRfAdvancedConnector = cfg.getInt(CATEGORY_GENERAL, "maxRfAdvancedConnector", maxRfAdvancedConnector, 1, 1000000000,
                "Maximum RF the advanced connector can store");

        controllerRFT = cfg.getInt(CATEGORY_GENERAL, "controllerRFPerTick", controllerRFT, 0, 1000000000,
                "Power usage for the controller regardless of what it is doing");
        controllerChannelRFT = cfg.getInt(CATEGORY_GENERAL, "controllerChannelRFT", controllerChannelRFT, 0, 1000000000,
                "Power usage for the controller per active channel");
        controllerOperationRFT = cfg.getInt(CATEGORY_GENERAL, "controllerOperationRFT", controllerOperationRFT, 0, 1000000000,
                "Power usage for the controller per operation performed by one of the channels");

    }
}
