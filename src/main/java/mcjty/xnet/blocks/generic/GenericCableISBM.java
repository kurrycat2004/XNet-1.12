package mcjty.xnet.blocks.generic;

import com.google.common.base.Function;
import mcjty.xnet.XNet;
import mcjty.xnet.blocks.test.ConnectorType;
import mcjty.xnet.blocks.test.EnergyConnectorBlock;
import mcjty.xnet.blocks.test.ItemConnectorBlock;
import mcjty.xnet.blocks.test.NetCableBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericCableISBM implements IBakedModel {

    public static final ModelResourceLocation modelEnergyConnector = new ModelResourceLocation(XNet.MODID + ":" + EnergyConnectorBlock.ENERGY_CONNECTOR);
    public static final ModelResourceLocation modelItemConnector = new ModelResourceLocation(XNet.MODID + ":" + ItemConnectorBlock.ITEM_CONNECTOR);
    public static final ModelResourceLocation modelCable = new ModelResourceLocation(XNet.MODID + ":" + NetCableBlock.NETCABLE);

    private TextureAtlasSprite sprite;

    private TextureAtlasSprite spriteCable;
    private TextureAtlasSprite spriteEnergy;
    private TextureAtlasSprite spriteSide;

    private VertexFormat format;

    public GenericCableISBM(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        this.format = format;
        sprite = bakedTextureGetter.apply(new ResourceLocation(XNet.MODID, "blocks/normal_netcable"));
    }

    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v) {
        for (int e = 0; e < format.getElementCount(); e++) {
            switch (format.getElement(e).getUsage()) {
                case POSITION:
                    builder.put(e, (float)x, (float)y, (float)z, 1.0f);
                    break;
                case COLOR:
                    builder.put(e, 1.0f, 1.0f, 1.0f, 1.0f);
                    break;
                case UV:
                    if (format.getElement(e).getIndex() == 0) {
                        u = sprite.getInterpolatedU(u);
                        v = sprite.getInterpolatedV(v);
                        builder.put(e, u, v, 0f, 1f);
                        break;
                    }
                case NORMAL:
                    builder.put(e, (float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord, 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {
        Vec3d normal = v3.subtract(v2).crossProduct(v1.subtract(v2)).normalize();

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.xCoord, v1.yCoord, v1.zCoord, 0, 0);
        putVertex(builder, normal, v2.xCoord, v2.yCoord, v2.zCoord, 0, 16);
        putVertex(builder, normal, v3.xCoord, v3.yCoord, v3.zCoord, 16, 16);
        putVertex(builder, normal, v4.xCoord, v4.yCoord, v4.zCoord, 16, 0);
        return builder.build();
    }


    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {

        if (side != null) {
            return Collections.emptyList();
        }

        // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
        // our baked model implementation.
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        ConnectorType north, south, west, east, up, down;
        north = extendedBlockState.getValue(GenericCableBlock.NORTH);
        south = extendedBlockState.getValue(GenericCableBlock.SOUTH);
        west = extendedBlockState.getValue(GenericCableBlock.WEST);
        east = extendedBlockState.getValue(GenericCableBlock.EAST);
        up = extendedBlockState.getValue(GenericCableBlock.UP);
        down = extendedBlockState.getValue(GenericCableBlock.DOWN);

        spriteCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(XNet.MODID + ":blocks/netcable");
        GenericCableBlock block = (GenericCableBlock) state.getBlock();
        String connectorTexture = block.getConnectorTexture();
        if (connectorTexture != null) {
            spriteEnergy = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(connectorTexture);
        }
        spriteSide = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(XNet.MODID + ":blocks/connectorSide");

        List<BakedQuad> quads = new ArrayList<>();

        double o = .4;      // Thickness of the cable. .0 would be full block, .5 is infinitely thin.
        double p = .1;      // Thickness of the connector as it is put on the connecting block
        double q = .2;      // The wideness of the connector

        // For each side we either cap it off if there is no similar block adjacent on that side
        // or else we extend so that we touch the adjacent block:

        if (up == ConnectorType.CABLE) {
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, o),     new Vec3d(1 - o, 1,     o),     new Vec3d(1 - o, 1,     1 - o), new Vec3d(1 - o, 1 - o, 1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(o,     1,     1 - o), new Vec3d(o,     1,     o),     new Vec3d(o,     1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     1,     o),     new Vec3d(1 - o, 1,     o),     new Vec3d(1 - o, 1 - o, o),     new Vec3d(o,     1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1,     1 - o), new Vec3d(o,     1,     1 - o), spriteCable));
        } else if (up == ConnectorType.BLOCK) {
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, o),     new Vec3d(1 - o, 1 - p, o),     new Vec3d(1 - o, 1 - p, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(o,     1 - p, 1 - o), new Vec3d(o,     1 - p, o),     new Vec3d(o,     1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     1 - p, o),     new Vec3d(1 - o, 1 - p, o),     new Vec3d(1 - o, 1 - o, o),     new Vec3d(o,     1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - p, 1 - o), new Vec3d(o,     1 - p, 1 - o), spriteCable));

            quads.add(createQuad(new Vec3d(1 - q, 1 - p, q),     new Vec3d(1 - q, 1,     q),     new Vec3d(1 - q, 1,     1 - q), new Vec3d(1 - q, 1 - p, 1 - q), spriteSide));
            quads.add(createQuad(new Vec3d(q,     1 - p, 1 - q), new Vec3d(q,     1,     1 - q), new Vec3d(q,     1,     q),     new Vec3d(q,     1 - p, q), spriteSide));
            quads.add(createQuad(new Vec3d(q,     1,     q),     new Vec3d(1 - q, 1,     q),     new Vec3d(1 - q, 1 - p, q),     new Vec3d(q,     1 - p, q), spriteSide));
            quads.add(createQuad(new Vec3d(q,     1 - p, 1 - q), new Vec3d(1 - q, 1 - p, 1 - q), new Vec3d(1 - q, 1,     1 - q), new Vec3d(q,     1,     1 - q), spriteSide));

            quads.add(createQuad(new Vec3d(q,     1 - p, q),     new Vec3d(1 - q, 1 - p, q),     new Vec3d(1 - q, 1 - p, 1 - q), new Vec3d(q,     1 - p, 1 - q), spriteEnergy));
        } else {
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, o),     new Vec3d(o,     1 - o, o), spriteCable));
        }

        if (down == ConnectorType.CABLE) {
            quads.add(createQuad(new Vec3d(1 - o, 0, o),     new Vec3d(1 - o, o, o),     new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 0, 1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     0, 1 - o), new Vec3d(o,     o, 1 - o), new Vec3d(o,     o, o),     new Vec3d(o,     0, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o, o),     new Vec3d(1 - o, o, o),     new Vec3d(1 - o, 0, o),     new Vec3d(o,     0, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     0, 1 - o), new Vec3d(1 - o, 0, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o,     o, 1 - o), spriteCable));
        } else if (down == ConnectorType.BLOCK) {
            quads.add(createQuad(new Vec3d(1 - o, p, o),     new Vec3d(1 - o, o, o),     new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, p, 1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     p, 1 - o), new Vec3d(o,     o, 1 - o), new Vec3d(o,     o, o),     new Vec3d(o,     p, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o, o),     new Vec3d(1 - o, o, o),     new Vec3d(1 - o, p, o),     new Vec3d(o,     p, o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     p, 1 - o), new Vec3d(1 - o, p, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o,     o, 1 - o), spriteCable));

            quads.add(createQuad(new Vec3d(1 - q, 0, q),     new Vec3d(1 - q, p, q),     new Vec3d(1 - q, p, 1 - q), new Vec3d(1 - q, 0, 1 - q), spriteSide));
            quads.add(createQuad(new Vec3d(q,     0, 1 - q), new Vec3d(q,     p, 1 - q), new Vec3d(q,     p, q),     new Vec3d(q,     0, q), spriteSide));
            quads.add(createQuad(new Vec3d(q,     p, q),     new Vec3d(1 - q, p, q),     new Vec3d(1 - q, 0, q),     new Vec3d(q,     0, q), spriteSide));
            quads.add(createQuad(new Vec3d(q,     0, 1 - q), new Vec3d(1 - q, 0, 1 - q), new Vec3d(1 - q, p, 1 - q), new Vec3d(q,     p, 1 - q), spriteSide));

            quads.add(createQuad(new Vec3d(q,     p, 1 - q), new Vec3d(1 - q, p, 1 - q), new Vec3d(1 - q, p, q),     new Vec3d(q,     p, q), spriteEnergy));
        } else {
            quads.add(createQuad(new Vec3d(o, o, o), new Vec3d(1 - o, o, o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o, o, 1 - o), spriteCable));
        }

        if (east == ConnectorType.CABLE) {
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1, 1 - o, 1 - o), new Vec3d(1, 1 - o, o),     new Vec3d(1 - o, 1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     o),     new Vec3d(1, o,     o),     new Vec3d(1, o,     1 - o), new Vec3d(1 - o, o,     1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, o),     new Vec3d(1, 1 - o, o),     new Vec3d(1, o,     o),     new Vec3d(1 - o, o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     1 - o), new Vec3d(1, o,     1 - o), new Vec3d(1, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), spriteCable));
        } else if (east == ConnectorType.BLOCK) {
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - p, 1 - o, 1 - o), new Vec3d(1 - p, 1 - o, o),     new Vec3d(1 - o, 1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     o),     new Vec3d(1 - p, o,     o),     new Vec3d(1 - p, o,     1 - o), new Vec3d(1 - o, o,     1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, 1 - o, o),     new Vec3d(1 - p, 1 - o, o),     new Vec3d(1 - p, o,     o),     new Vec3d(1 - o, o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - p, o,     1 - o), new Vec3d(1 - p, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), spriteCable));

            quads.add(createQuad(new Vec3d(1 - p, 1 - q, 1 - q), new Vec3d(1, 1 - q, 1 - q), new Vec3d(1, 1 - q, q),     new Vec3d(1 - p, 1 - q, q), spriteSide));
            quads.add(createQuad(new Vec3d(1 - p, q,     q),     new Vec3d(1, q,     q),     new Vec3d(1, q,     1 - q), new Vec3d(1 - p, q,     1 - q), spriteSide));
            quads.add(createQuad(new Vec3d(1 - p, 1 - q, q),     new Vec3d(1, 1 - q, q),     new Vec3d(1, q,     q),     new Vec3d(1 - p, q,     q), spriteSide));
            quads.add(createQuad(new Vec3d(1 - p, q,     1 - q), new Vec3d(1, q,     1 - q), new Vec3d(1, 1 - q, 1 - q), new Vec3d(1 - p, 1 - q, 1 - q), spriteSide));

            quads.add(createQuad(new Vec3d(1 - p, q, 1 - q), new Vec3d(1 - p, 1 - q, 1 - q), new Vec3d(1 - p, 1 - q, q), new Vec3d(1 - p, q, q), spriteEnergy));
        } else {
            quads.add(createQuad(new Vec3d(1 - o, o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, o, 1 - o), spriteCable));
        }

        if (west == ConnectorType.CABLE) {
            quads.add(createQuad(new Vec3d(0, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o),     new Vec3d(0, 1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(0, o,     o),     new Vec3d(o, o,     o),     new Vec3d(o, o,     1 - o), new Vec3d(0, o,     1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(0, 1 - o, o),     new Vec3d(o, 1 - o, o),     new Vec3d(o, o,     o),     new Vec3d(0, o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(0, o,     1 - o), new Vec3d(o, o,     1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(0, 1 - o, 1 - o), spriteCable));
        } else if (west == ConnectorType.BLOCK) {
            quads.add(createQuad(new Vec3d(p, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o),     new Vec3d(p, 1 - o, o), spriteCable));
            quads.add(createQuad(new Vec3d(p, o,     o),     new Vec3d(o, o,     o),     new Vec3d(o, o,     1 - o), new Vec3d(p, o,     1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(p, 1 - o, o),     new Vec3d(o, 1 - o, o),     new Vec3d(o, o,     o),     new Vec3d(p, o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(p, o,     1 - o), new Vec3d(o, o,     1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(p, 1 - o, 1 - o), spriteCable));

            quads.add(createQuad(new Vec3d(0, 1 - q, 1 - q), new Vec3d(p, 1 - q, 1 - q), new Vec3d(p, 1 - q, q),     new Vec3d(0, 1 - q, q), spriteSide));
            quads.add(createQuad(new Vec3d(0, q,     q),     new Vec3d(p, q,     q),     new Vec3d(p, q,     1 - q), new Vec3d(0, q,     1 - q), spriteSide));
            quads.add(createQuad(new Vec3d(0, 1 - q, q),     new Vec3d(p, 1 - q, q),     new Vec3d(p, q,     q),     new Vec3d(0, q,     q), spriteSide));
            quads.add(createQuad(new Vec3d(0, q,     1 - q), new Vec3d(p, q,     1 - q), new Vec3d(p, 1 - q, 1 - q), new Vec3d(0, 1 - q, 1 - q), spriteSide));

            quads.add(createQuad(new Vec3d(p, q, q), new Vec3d(p, 1 - q, q), new Vec3d(p, 1 - q, 1 - q), new Vec3d(p, q, 1 - q), spriteEnergy));
        } else {
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o), new Vec3d(o, o, o), spriteCable));
        }

        if (north == ConnectorType.CABLE) {
            quads.add(createQuad(new Vec3d(o,     1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 0), new Vec3d(o,     1 - o, 0), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     0), new Vec3d(1 - o, o,     0), new Vec3d(1 - o, o,     o), new Vec3d(o,     o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     0), new Vec3d(1 - o, 1 - o, 0), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     o), new Vec3d(o,     1 - o, o), new Vec3d(o,     1 - o, 0), new Vec3d(o,     o,     0), spriteCable));
        } else if (north == ConnectorType.BLOCK) {
            quads.add(createQuad(new Vec3d(o,     1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, p), new Vec3d(o,     1 - o, p), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     p), new Vec3d(1 - o, o,     p), new Vec3d(1 - o, o,     o), new Vec3d(o,     o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     p), new Vec3d(1 - o, 1 - o, p), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o,     o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     o), new Vec3d(o,     1 - o, o), new Vec3d(o,     1 - o, p), new Vec3d(o,     o,     p), spriteCable));

            quads.add(createQuad(new Vec3d(q,     1 - q, p), new Vec3d(1 - q, 1 - q, p), new Vec3d(1 - q, 1 - q, 0), new Vec3d(q,     1 - q, 0), spriteSide));
            quads.add(createQuad(new Vec3d(q,     q,     0), new Vec3d(1 - q, q,     0), new Vec3d(1 - q, q,     p), new Vec3d(q,     q,     p), spriteSide));
            quads.add(createQuad(new Vec3d(1 - q, q,     0), new Vec3d(1 - q, 1 - q, 0), new Vec3d(1 - q, 1 - q, p), new Vec3d(1 - q, q,     p), spriteSide));
            quads.add(createQuad(new Vec3d(q,     q,     p), new Vec3d(q,     1 - q, p), new Vec3d(q,     1 - q, 0), new Vec3d(q,     q,     0), spriteSide));

            quads.add(createQuad(new Vec3d(q, q, p), new Vec3d(1 - q, q, p), new Vec3d(1 - q, 1 - q, p), new Vec3d(q, 1 - q, p), spriteEnergy));
        } else {
            quads.add(createQuad(new Vec3d(o, 1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o, o), new Vec3d(o, o, o), spriteCable));
        }

        if (south == ConnectorType.CABLE) {
            quads.add(createQuad(new Vec3d(o,     1 - o, 1),     new Vec3d(1 - o, 1 - o, 1),     new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o,     1 - o, 1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     1 - o), new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, o,     1),     new Vec3d(o,     o,     1), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1),     new Vec3d(1 - o, o,     1), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     1),     new Vec3d(o,     1 - o, 1),     new Vec3d(o,     1 - o, 1 - o), new Vec3d(o,     o,     1 - o), spriteCable));
        } else if (south == ConnectorType.BLOCK) {
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - p), new Vec3d(1 - o, 1 - o, 1 - p), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o,     1 - o, 1 - o), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     1 - o), new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, o,     1 - p), new Vec3d(o,     o,     1 - p), spriteCable));
            quads.add(createQuad(new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - p), new Vec3d(1 - o, o,     1 - p), spriteCable));
            quads.add(createQuad(new Vec3d(o,     o,     1 - p), new Vec3d(o,     1 - o, 1 - p), new Vec3d(o,     1 - o, 1 - o), new Vec3d(o,     o,     1 - o), spriteCable));

            quads.add(createQuad(new Vec3d(q,     1 - q, 1),     new Vec3d(1 - q, 1 - q, 1),     new Vec3d(1 - q, 1 - q, 1 - p), new Vec3d(q,     1 - q, 1 - p), spriteSide));
            quads.add(createQuad(new Vec3d(q,     q,     1 - p), new Vec3d(1 - q, q,     1 - p), new Vec3d(1 - q, q,     1),     new Vec3d(q,     q,     1), spriteSide));
            quads.add(createQuad(new Vec3d(1 - q, q,     1 - p), new Vec3d(1 - q, 1 - q, 1 - p), new Vec3d(1 - q, 1 - q, 1),     new Vec3d(1 - q, q,     1), spriteSide));
            quads.add(createQuad(new Vec3d(q,     q,     1),     new Vec3d(q,     1 - q, 1),     new Vec3d(q,     1 - q, 1 - p), new Vec3d(q,     q,     1 - p), spriteSide));

            quads.add(createQuad(new Vec3d(q, 1 - q, 1 - p), new Vec3d(1 - q, 1 - q, 1 - p), new Vec3d(1 - q, q, 1 - p), new Vec3d(q, q, 1 - p), spriteEnergy));
        } else {
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), spriteCable));
        }



        return quads;
//        return new BakedModel(north, south, west, east, up, down, (GenericCableBlock) state.getBlock());
    }


    @Override
    public boolean isAmbientOcclusion() {
        return false;
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
        return sprite;
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
