package mcjty.xnet.client;

import elec332.core.client.IIconRegistrar;
import elec332.core.client.model.ElecModelBakery;
import elec332.core.client.model.ElecQuadBakery;
import elec332.core.client.model.model.IModelAndTextureLoader;
import elec332.core.client.model.template.ElecTemplateBakery;
import mcjty.xnet.varia.XNetResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static mcjty.xnet.init.ModItems.*;

/**
 * Created by Elec332 on 4-3-2016.
 */
public class XNetClientModelLoader implements IModelAndTextureLoader {

    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteSide;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteEnergy;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteItem;


    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteTerminal;

    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteAdvancedCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteAdvancedCornerCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteAdvancedEndCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteAdvancedThreeCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteAdvancedCrossCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteAdvancedNoneCable;

    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteCornerCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteEndCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteThreeCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteCrossCable;
    @SuppressWarnings("PublicField")
    public static TextureAtlasSprite spriteNoneCable;

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     *
     * @param quadBakery     The QuadBakery.
     * @param modelBakery    The ModelBakery
     * @param templateBakery The TemplateBakery
     */
    @Override
    public void registerModels(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery) {

    }

    /**
     * Use this to register your textures.
     *
     * @param iconRegistrar The IIconRegistrar.
     */
    @Override
    public void registerTextures(IIconRegistrar iconRegistrar) {
        spriteSide = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/connectorSide"));
        spriteCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/normalNetcable"));
        spriteCornerCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/normalCornerNetcable"));
        spriteEndCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/normalEndNetcable"));
        spriteThreeCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/normalThreeNetcable"));
        spriteCrossCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/normalCrossNetcable"));
        spriteNoneCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/normalNoneNetcable"));
        spriteAdvancedCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/advancedNetcable"));
        spriteAdvancedCornerCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/advancedCornerNetcable"));
        spriteAdvancedEndCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/advancedEndNetcable"));
        spriteAdvancedThreeCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/advancedThreeNetcable"));
        spriteAdvancedCrossCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/advancedCrossNetcable"));
        spriteAdvancedNoneCable = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/advancedNoneNetcable"));
        spriteEnergy = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/energyConnector"));
        spriteItem = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/itemConnector"));
        spriteTerminal = iconRegistrar.registerSprite(new XNetResourceLocation("blocks/terminal"));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
        event.modelRegistry.putObject(new ModelResourceLocation("xnet:netcable#multipart"), new AdvancedCableISBM(false));
        event.modelRegistry.putObject(new ModelResourceLocation("xnet:advanced_netcable#multipart"), new AdvancedCableISBM(true));
        event.modelRegistry.putObject(new ModelResourceLocation("xnet:rfconnector#multipart"), new ConnectorISBM(spriteEnergy));
        event.modelRegistry.putObject(new ModelResourceLocation("xnet:itemconnector#multipart"), new ConnectorISBM(spriteItem));
        event.modelRegistry.putObject(new ModelResourceLocation("xnet:terminal#multipart"), new TerminalISBM(spriteTerminal));
    }

    public void setModelLocations(){
        ModelLoader.setCustomModelResourceLocation(cable, 0, new ModelResourceLocation(cable.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(advancedCable, 0, new ModelResourceLocation(advancedCable.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(energyConnector, 0, new ModelResourceLocation(energyConnector.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemConnector, 0, new ModelResourceLocation(itemConnector.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(terminal, 0, new ModelResourceLocation(terminal.getRegistryName(), "inventory"));
    }

}
