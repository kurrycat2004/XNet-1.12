package mcjty.xnet.blocks.generic;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import mcjty.xnet.XNet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;

public class GenericCableModel implements IModel {
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new GenericCableBakedModel(state, format, bakedTextureGetter);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.of(
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable0/normal_three_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable1/normal_three_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable2/normal_three_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_corner_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_cross_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_end_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_none_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/cable3/normal_three_netcable"),
                new ResourceLocation(XNet.MODID, "blocks/connector_side"),
                new ResourceLocation(XNet.MODID, "blocks/advanced_connector"),
                new ResourceLocation(XNet.MODID, "blocks/connector")
                );
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
