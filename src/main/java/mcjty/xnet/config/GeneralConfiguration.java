package mcjty.xnet.config;

import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {
    public static final String CATEGORY_GENERAL = "general";

    public static int maxRfConnector = 50000;
    public static int maxRfAdvancedConnector = 500000;

    public static void init(Configuration cfg) {

        maxRfConnector = cfg.getInt(CATEGORY_GENERAL, "maxRfConnector", maxRfConnector, 1, 1000000,
                "Maximum RF the normal connector can store");
        maxRfAdvancedConnector = cfg.getInt(CATEGORY_GENERAL, "maxRfAdvancedConnector", maxRfAdvancedConnector, 1, 1000000,
                "Maximum RF the advanced connector can store");
}
}
