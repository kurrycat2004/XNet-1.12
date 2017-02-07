package mcjty.xnet.blocks.test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.generic.GenericCableISBM;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;

public class GenericModel implements IModel {
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new GenericCableISBM(state, format, bakedTextureGetter);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(new ResourceLocation(XNet.MODID, "blocks/normal_netcable"));
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
