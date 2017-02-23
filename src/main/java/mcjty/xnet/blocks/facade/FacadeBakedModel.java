package mcjty.xnet.blocks.facade;

import com.google.common.base.Function;
import mcjty.xnet.XNet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class FacadeBakedModel implements IBakedModel {

    public static final ModelResourceLocation modelFacade = new ModelResourceLocation(XNet.MODID + ":" + FacadeBlock.FACADE);

    private VertexFormat format;
    private static TextureAtlasSprite spriteCable;

    public FacadeBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
    }

    private static void initTextures() {
        if (spriteCable == null) {
            spriteCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(XNet.MODID + ":blocks/facade");
        }
    }



    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        FacadeBlockId facadeId = extendedBlockState.getValue(FacadeBlock.FACADEID);
        if (facadeId == null) {
            return Collections.emptyList();
        }

        IBakedModel model = getModel(facadeId);
        return model.getQuads(state, side, rand);
    }

    private IBakedModel getModel(@Nonnull FacadeBlockId facadeId) {
        initTextures();
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(facadeId.getRegistryName()));
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(block.getStateFromMeta(facadeId.getMeta()));
        return model;
    }


    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return spriteCable;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }

}
