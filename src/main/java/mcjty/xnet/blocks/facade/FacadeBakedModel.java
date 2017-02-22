package mcjty.xnet.blocks.facade;

import com.google.common.base.Function;
import mcjty.xnet.XNet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.List;

public class FacadeBakedModel implements IBakedModel {

    public static final ModelResourceLocation modelFacade = new ModelResourceLocation(XNet.MODID + ":" + FacadeBlock.FACADE);

    private VertexFormat format;
    private IBakedModel facadeModel = null;

    public FacadeBakedModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        IBakedModel model = getModel();
        return model.getQuads(state, side, rand);
    }

    private IBakedModel getModel() {
        if (facadeModel == null) {
            facadeModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.COBBLESTONE.getDefaultState());
        }
        return facadeModel;
    }


    @Override
    public boolean isAmbientOcclusion() {
        return getModel().isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return getModel().isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return getModel().isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        IBakedModel model = getModel();
        return model.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return getModel().getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return getModel().getOverrides();
    }

}
