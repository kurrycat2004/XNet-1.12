package mcjty.xnet.blocks;

import com.google.common.primitives.Ints;
import mcjty.xnet.XNet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericCableISBM implements ISmartBlockModel {

    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(XNet.MODID + ":energy_connector");

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
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
        return new BakedModel(north, south, west, east, up, down);
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<BakedQuad> getGeneralQuads() {
        throw new UnsupportedOperationException();
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
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return null;
    }

    public class BakedModel implements IBakedModel {
        private TextureAtlasSprite spriteCable;
        private TextureAtlasSprite spriteConnector;
        private TextureAtlasSprite spriteSide;

        private final ConnectorType north;
        private final ConnectorType south;
        private final ConnectorType west;
        private final ConnectorType east;
        private final ConnectorType up;
        private final ConnectorType down;

        public BakedModel(ConnectorType north, ConnectorType south, ConnectorType west, ConnectorType east, ConnectorType up, ConnectorType down) {
            spriteCable = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(XNet.MODID + ":blocks/netcable");
            spriteConnector = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(XNet.MODID + ":blocks/energyConnector");
            spriteSide = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(XNet.MODID + ":blocks/machineTop");
            this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.up = up;
            this.down = down;
        }

        private int[] vertexToInts(double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
            return new int[] {
                    Float.floatToRawIntBits((float) x),
                    Float.floatToRawIntBits((float) y),
                    Float.floatToRawIntBits((float) z),
                    -1,
                    Float.floatToRawIntBits(sprite.getInterpolatedU(u)),
                    Float.floatToRawIntBits(sprite.getInterpolatedV(v)),
                    0
            };
        }

        private BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
            Vec3 normal = v1.subtract(v2).crossProduct(v3.subtract(v2));
            EnumFacing side = LightUtil.toSide((float) normal.xCoord, (float) normal.yCoord, (float) normal.zCoord);

            return new BakedQuad(Ints.concat(
                    vertexToInts(v1.xCoord, v1.yCoord, v1.zCoord, 0, 0, sprite),
                    vertexToInts(v2.xCoord, v2.yCoord, v2.zCoord, 0, 16, sprite),
                    vertexToInts(v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, sprite),
                    vertexToInts(v4.xCoord, v4.yCoord, v4.zCoord, 16, 0, sprite)
            ), -1, side);
        }

        @Override
        public List<BakedQuad> getFaceQuads(EnumFacing side) {
            return Collections.emptyList();
        }

        @Override
        public List<BakedQuad> getGeneralQuads() {
            List<BakedQuad> quads = new ArrayList<>();
            double o = .4;      // Thickness of the cable. .0 would be full block, .5 is infinitely thin.
            double p = .1;      // Thickness of the connector as it is put on the connecting block
            double q = .2;      // The wideness of the connector

            // For each side we either cap it off if there is no similar block adjacent on that side
            // or else we extend so that we touch the adjacent block:

            if (up == ConnectorType.CABLE) {
                quads.add(createQuad(new Vec3(1 - o, 1 - o, o),     new Vec3(1 - o, 1,     o),     new Vec3(1 - o, 1,     1 - o), new Vec3(1 - o, 1 - o, 1 - o), spriteCable));
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(o,     1,     1 - o), new Vec3(o,     1,     o),     new Vec3(o,     1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     1,     o),     new Vec3(1 - o, 1,     o),     new Vec3(1 - o, 1 - o, o),     new Vec3(o,     1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1,     1 - o), new Vec3(o,     1,     1 - o), spriteCable));
            } else if (up == ConnectorType.BLOCK) {
                quads.add(createQuad(new Vec3(1 - o, 1 - o, o),     new Vec3(1 - o, 1 - p, o),     new Vec3(1 - o, 1 - p, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), spriteCable));
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(o,     1 - p, 1 - o), new Vec3(o,     1 - p, o),     new Vec3(o,     1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     1 - p, o),     new Vec3(1 - o, 1 - p, o),     new Vec3(1 - o, 1 - o, o),     new Vec3(o,     1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - p, 1 - o), new Vec3(o,     1 - p, 1 - o), spriteCable));

                quads.add(createQuad(new Vec3(1 - q, 1 - p, q),     new Vec3(1 - q, 1,     q),     new Vec3(1 - q, 1,     1 - q), new Vec3(1 - q, 1 - p, 1 - q), spriteSide));
                quads.add(createQuad(new Vec3(q,     1 - p, 1 - q), new Vec3(q,     1,     1 - q), new Vec3(q,     1,     q),     new Vec3(q,     1 - p, q), spriteSide));
                quads.add(createQuad(new Vec3(q,     1,     q),     new Vec3(1 - q, 1,     q),     new Vec3(1 - q, 1 - p, q),     new Vec3(q,     1 - p, q), spriteSide));
                quads.add(createQuad(new Vec3(q,     1 - p, 1 - q), new Vec3(1 - q, 1 - p, 1 - q), new Vec3(1 - q, 1,     1 - q), new Vec3(q,     1,     1 - q), spriteSide));

                quads.add(createQuad(new Vec3(q,     1 - p, q),     new Vec3(1 - q, 1 - p, q),     new Vec3(1 - q, 1 - p, 1 - q), new Vec3(q,     1 - p, 1 - q), spriteConnector));
            } else {
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, o),     new Vec3(o,     1 - o, o), spriteCable));
            }

            if (down == ConnectorType.CABLE) {
                quads.add(createQuad(new Vec3(1 - o, 0, o),     new Vec3(1 - o, o, o),     new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 0, 1 - o), spriteCable));
                quads.add(createQuad(new Vec3(o,     0, 1 - o), new Vec3(o,     o, 1 - o), new Vec3(o,     o, o),     new Vec3(o,     0, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     o, o),     new Vec3(1 - o, o, o),     new Vec3(1 - o, 0, o),     new Vec3(o,     0, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     0, 1 - o), new Vec3(1 - o, 0, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(o,     o, 1 - o), spriteCable));
            } else if (down == ConnectorType.BLOCK) {
                quads.add(createQuad(new Vec3(1 - o, p, o),     new Vec3(1 - o, o, o),     new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, p, 1 - o), spriteCable));
                quads.add(createQuad(new Vec3(o,     p, 1 - o), new Vec3(o,     o, 1 - o), new Vec3(o,     o, o),     new Vec3(o,     p, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     o, o),     new Vec3(1 - o, o, o),     new Vec3(1 - o, p, o),     new Vec3(o,     p, o), spriteCable));
                quads.add(createQuad(new Vec3(o,     p, 1 - o), new Vec3(1 - o, p, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(o,     o, 1 - o), spriteCable));

                quads.add(createQuad(new Vec3(1 - q, 0, q),     new Vec3(1 - q, p, q),     new Vec3(1 - q, p, 1 - q), new Vec3(1 - q, 0, 1 - q), spriteSide));
                quads.add(createQuad(new Vec3(q,     0, 1 - q), new Vec3(q,     p, 1 - q), new Vec3(q,     p, q),     new Vec3(q,     0, q), spriteSide));
                quads.add(createQuad(new Vec3(q,     p, q),     new Vec3(1 - q, p, q),     new Vec3(1 - q, 0, q),     new Vec3(q,     0, q), spriteSide));
                quads.add(createQuad(new Vec3(q,     0, 1 - q), new Vec3(1 - q, 0, 1 - q), new Vec3(1 - q, p, 1 - q), new Vec3(q,     p, 1 - q), spriteSide));

                quads.add(createQuad(new Vec3(q,     p, 1 - q), new Vec3(1 - q, p, 1 - q), new Vec3(1 - q, p, q),     new Vec3(q,     p, q), spriteConnector));
            } else {
                quads.add(createQuad(new Vec3(o, o, o), new Vec3(1 - o, o, o), new Vec3(1 - o, o, 1 - o), new Vec3(o, o, 1 - o), spriteCable));
            }

            if (east == ConnectorType.CABLE) {
                quads.add(createQuad(new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1, 1 - o, 1 - o), new Vec3(1, 1 - o, o),     new Vec3(1 - o, 1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     o),     new Vec3(1, o,     o),     new Vec3(1, o,     1 - o), new Vec3(1 - o, o,     1 - o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, 1 - o, o),     new Vec3(1, 1 - o, o),     new Vec3(1, o,     o),     new Vec3(1 - o, o,     o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     1 - o), new Vec3(1, o,     1 - o), new Vec3(1, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), spriteCable));
            } else if (east == ConnectorType.BLOCK) {
                quads.add(createQuad(new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - p, 1 - o, 1 - o), new Vec3(1 - p, 1 - o, o),     new Vec3(1 - o, 1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     o),     new Vec3(1 - p, o,     o),     new Vec3(1 - p, o,     1 - o), new Vec3(1 - o, o,     1 - o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, 1 - o, o),     new Vec3(1 - p, 1 - o, o),     new Vec3(1 - p, o,     o),     new Vec3(1 - o, o,     o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     1 - o), new Vec3(1 - p, o,     1 - o), new Vec3(1 - p, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), spriteCable));

                quads.add(createQuad(new Vec3(1 - p, 1 - q, 1 - q), new Vec3(1, 1 - q, 1 - q), new Vec3(1, 1 - q, q),     new Vec3(1 - p, 1 - q, q), spriteSide));
                quads.add(createQuad(new Vec3(1 - p, q,     q),     new Vec3(1, q,     q),     new Vec3(1, q,     1 - q), new Vec3(1 - p, q,     1 - q), spriteSide));
                quads.add(createQuad(new Vec3(1 - p, 1 - q, q),     new Vec3(1, 1 - q, q),     new Vec3(1, q,     q),     new Vec3(1 - p, q,     q), spriteSide));
                quads.add(createQuad(new Vec3(1 - p, q,     1 - q), new Vec3(1, q,     1 - q), new Vec3(1, 1 - q, 1 - q), new Vec3(1 - p, 1 - q, 1 - q), spriteSide));

                quads.add(createQuad(new Vec3(1 - p, q, 1 - q), new Vec3(1 - p, 1 - q, 1 - q), new Vec3(1 - p, 1 - q, q), new Vec3(1 - p, q, q), spriteConnector));
            } else {
                quads.add(createQuad(new Vec3(1 - o, o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, o, 1 - o), spriteCable));
            }

            if (west == ConnectorType.CABLE) {
                quads.add(createQuad(new Vec3(0, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o),     new Vec3(0, 1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(0, o,     o),     new Vec3(o, o,     o),     new Vec3(o, o,     1 - o), new Vec3(0, o,     1 - o), spriteCable));
                quads.add(createQuad(new Vec3(0, 1 - o, o),     new Vec3(o, 1 - o, o),     new Vec3(o, o,     o),     new Vec3(0, o,     o), spriteCable));
                quads.add(createQuad(new Vec3(0, o,     1 - o), new Vec3(o, o,     1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(0, 1 - o, 1 - o), spriteCable));
            } else if (west == ConnectorType.BLOCK) {
                quads.add(createQuad(new Vec3(p, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o),     new Vec3(p, 1 - o, o), spriteCable));
                quads.add(createQuad(new Vec3(p, o,     o),     new Vec3(o, o,     o),     new Vec3(o, o,     1 - o), new Vec3(p, o,     1 - o), spriteCable));
                quads.add(createQuad(new Vec3(p, 1 - o, o),     new Vec3(o, 1 - o, o),     new Vec3(o, o,     o),     new Vec3(p, o,     o), spriteCable));
                quads.add(createQuad(new Vec3(p, o,     1 - o), new Vec3(o, o,     1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(p, 1 - o, 1 - o), spriteCable));

                quads.add(createQuad(new Vec3(0, 1 - q, 1 - q), new Vec3(p, 1 - q, 1 - q), new Vec3(p, 1 - q, q),     new Vec3(0, 1 - q, q), spriteSide));
                quads.add(createQuad(new Vec3(0, q,     q),     new Vec3(p, q,     q),     new Vec3(p, q,     1 - q), new Vec3(0, q,     1 - q), spriteSide));
                quads.add(createQuad(new Vec3(0, 1 - q, q),     new Vec3(p, 1 - q, q),     new Vec3(p, q,     q),     new Vec3(0, q,     q), spriteSide));
                quads.add(createQuad(new Vec3(0, q,     1 - q), new Vec3(p, q,     1 - q), new Vec3(p, 1 - q, 1 - q), new Vec3(0, 1 - q, 1 - q), spriteSide));

                quads.add(createQuad(new Vec3(p, q, q), new Vec3(p, 1 - q, q), new Vec3(p, 1 - q, 1 - q), new Vec3(p, q, 1 - q), spriteConnector));
            } else {
                quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o), new Vec3(o, o, o), spriteCable));
            }

            if (north == ConnectorType.CABLE) {
                quads.add(createQuad(new Vec3(o,     1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 0), new Vec3(o,     1 - o, 0), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     0), new Vec3(1 - o, o,     0), new Vec3(1 - o, o,     o), new Vec3(o,     o,     o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     0), new Vec3(1 - o, 1 - o, 0), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o,     o), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     o), new Vec3(o,     1 - o, o), new Vec3(o,     1 - o, 0), new Vec3(o,     o,     0), spriteCable));
            } else if (north == ConnectorType.BLOCK) {
                quads.add(createQuad(new Vec3(o,     1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, p), new Vec3(o,     1 - o, p), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     p), new Vec3(1 - o, o,     p), new Vec3(1 - o, o,     o), new Vec3(o,     o,     o), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     p), new Vec3(1 - o, 1 - o, p), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o,     o), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     o), new Vec3(o,     1 - o, o), new Vec3(o,     1 - o, p), new Vec3(o,     o,     p), spriteCable));

                quads.add(createQuad(new Vec3(q,     1 - q, p), new Vec3(1 - q, 1 - q, p), new Vec3(1 - q, 1 - q, 0), new Vec3(q,     1 - q, 0), spriteSide));
                quads.add(createQuad(new Vec3(q,     q,     0), new Vec3(1 - q, q,     0), new Vec3(1 - q, q,     p), new Vec3(q,     q,     p), spriteSide));
                quads.add(createQuad(new Vec3(1 - q, q,     0), new Vec3(1 - q, 1 - q, 0), new Vec3(1 - q, 1 - q, p), new Vec3(1 - q, q,     p), spriteSide));
                quads.add(createQuad(new Vec3(q,     q,     p), new Vec3(q,     1 - q, p), new Vec3(q,     1 - q, 0), new Vec3(q,     q,     0), spriteSide));

                quads.add(createQuad(new Vec3(q, q, p), new Vec3(1 - q, q, p), new Vec3(1 - q, 1 - q, p), new Vec3(q, 1 - q, p), spriteConnector));
            } else {
                quads.add(createQuad(new Vec3(o, 1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o, o), new Vec3(o, o, o), spriteCable));
            }

            if (south == ConnectorType.CABLE) {
                quads.add(createQuad(new Vec3(o,     1 - o, 1),     new Vec3(1 - o, 1 - o, 1),     new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o,     1 - o, 1 - o), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     1 - o), new Vec3(1 - o, o,     1 - o), new Vec3(1 - o, o,     1),     new Vec3(o,     o,     1), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1),     new Vec3(1 - o, o,     1), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     1),     new Vec3(o,     1 - o, 1),     new Vec3(o,     1 - o, 1 - o), new Vec3(o,     o,     1 - o), spriteCable));
            } else if (south == ConnectorType.BLOCK) {
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - p), new Vec3(1 - o, 1 - o, 1 - p), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o,     1 - o, 1 - o), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     1 - o), new Vec3(1 - o, o,     1 - o), new Vec3(1 - o, o,     1 - p), new Vec3(o,     o,     1 - p), spriteCable));
                quads.add(createQuad(new Vec3(1 - o, o,     1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - p), new Vec3(1 - o, o,     1 - p), spriteCable));
                quads.add(createQuad(new Vec3(o,     o,     1 - p), new Vec3(o,     1 - o, 1 - p), new Vec3(o,     1 - o, 1 - o), new Vec3(o,     o,     1 - o), spriteCable));

                quads.add(createQuad(new Vec3(q,     1 - q, 1),     new Vec3(1 - q, 1 - q, 1),     new Vec3(1 - q, 1 - q, 1 - p), new Vec3(q,     1 - q, 1 - p), spriteSide));
                quads.add(createQuad(new Vec3(q,     q,     1 - p), new Vec3(1 - q, q,     1 - p), new Vec3(1 - q, q,     1),     new Vec3(q,     q,     1), spriteSide));
                quads.add(createQuad(new Vec3(1 - q, q,     1 - p), new Vec3(1 - q, 1 - q, 1 - p), new Vec3(1 - q, 1 - q, 1),     new Vec3(1 - q, q,     1), spriteSide));
                quads.add(createQuad(new Vec3(q,     q,     1),     new Vec3(q,     1 - q, 1),     new Vec3(q,     1 - q, 1 - p), new Vec3(q,     q,     1 - p), spriteSide));

                quads.add(createQuad(new Vec3(q, 1 - q, 1 - p), new Vec3(1 - q, 1 - q, 1 - p), new Vec3(1 - q, q, 1 - p), new Vec3(q, q, 1 - p), spriteConnector));
            } else {
                quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), spriteCable));
            }

            return quads;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return true;
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
    }
}
