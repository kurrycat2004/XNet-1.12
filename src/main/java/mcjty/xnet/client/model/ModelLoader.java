package mcjty.xnet.client.model;

import mcjty.xnet.XNet;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class ModelLoader implements ICustomModelLoader {

    public static final CableXNetModel CABLE_MODEL = new CableXNetModel();
    public static final AdvancedCableXNetModel ADVANCED_CABLE_MODEL = new AdvancedCableXNetModel();
    public static final RFConnectorXNetModel RF_CONNECTOR_MODEL = new RFConnectorXNetModel();
    public static final ItemConnectorXNetModel ITEM_CONNECTOR_MODEL = new ItemConnectorXNetModel();
    public static final TerminalXNetModel TERMINAL_MODEL = new TerminalXNetModel();

    private boolean matches(ResourceLocation loc, ModelResourceLocation model) {
        if (!model.getResourceDomain().equals(XNet.MODID)) {
            return false;
        }
        return model.getResourcePath().equals(loc.getResourcePath());
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (matches(modelLocation, AdvancedCableISBM.CABLE_MODEL)) {
            return true;
        }
        if (matches(modelLocation, AdvancedCableISBM.ADVANCED_CABLE_MODEL)) {
            return true;
        }
        if (matches(modelLocation, ConnectorISBM.RFCONNECTOR_MODEL)) {
            return true;
        }
        if (matches(modelLocation, ConnectorISBM.ITEMCONNECTOR_MODEL)) {
            return true;
        }
        if (matches(modelLocation, TerminalISBM.TERMINAL_MODEL)) {
            return true;
        }
        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        if (matches(modelLocation, AdvancedCableISBM.CABLE_MODEL)) {
            return CABLE_MODEL;
        }
        if (matches(modelLocation, AdvancedCableISBM.ADVANCED_CABLE_MODEL)) {
            return ADVANCED_CABLE_MODEL;
        }
        if (matches(modelLocation, ConnectorISBM.RFCONNECTOR_MODEL)) {
            return RF_CONNECTOR_MODEL;
        }
        if (matches(modelLocation, ConnectorISBM.ITEMCONNECTOR_MODEL)) {
            return ITEM_CONNECTOR_MODEL;
        }
        if (matches(modelLocation, TerminalISBM.TERMINAL_MODEL)) {
            return TERMINAL_MODEL;
        }
        return null;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
