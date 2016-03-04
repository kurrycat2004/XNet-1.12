package mcjty.xnet.client;

import com.google.common.primitives.Ints;
import mcmultipart.client.multipart.ISmartMultipartModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static mcjty.xnet.multipart.AbstractCableMultiPart.*;

public class CableISBM implements ISmartMultipartModel {

    public CableISBM() {
    }

    @Override
    public IBakedModel handlePartState(IBlockState state) {
        // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
        // our baked model implementation.
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        boolean north = extendedBlockState.getValue(NORTH);
        boolean south = extendedBlockState.getValue(SOUTH);
        boolean west = extendedBlockState.getValue(WEST);
        boolean east = extendedBlockState.getValue(EAST);
        boolean up = extendedBlockState.getValue(UP);
        boolean down = extendedBlockState.getValue(DOWN);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isGui3d() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBuiltInRenderer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        throw new UnsupportedOperationException();
    }

    private static class BakedModel implements IBakedModel {

        public BakedModel(boolean north, boolean south, boolean west, boolean east, boolean up, boolean down) {
            this.north = north;
            this.south = south;
            this.west = west;
            this.east = east;
            this.up = up;
            this.down = down;
        }

        private final boolean north;
        private final boolean south;
        private final boolean west;
        private final boolean east;
        private final boolean up;
        private final boolean down;

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

            // For each side we either cap it off if there is no similar block adjacent on that side
            // or else we extend so that we touch the adjacent block:

            TextureAtlasSprite sprite = XNetClientModelLoader.spriteCable;

            if (up) {
                quads.add(createQuad(new Vec3(1 - o, 1 - o, o),     new Vec3(1 - o, 1,     o),     new Vec3(1 - o, 1,     1 - o), new Vec3(1 - o, 1 - o, 1 - o), sprite));
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(o,     1,     1 - o), new Vec3(o,     1,     o),     new Vec3(o,     1 - o, o), sprite));
                quads.add(createQuad(new Vec3(o,     1,     o),     new Vec3(1 - o, 1,     o),     new Vec3(1 - o, 1 - o, o),     new Vec3(o,     1 - o, o), sprite));
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1,     1 - o), new Vec3(o,     1,     1 - o), sprite));
            } else {
                quads.add(createQuad(new Vec3(o,     1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, o),     new Vec3(o,     1 - o, o), sprite));
            }

            if (down) {
                quads.add(createQuad(new Vec3(1 - o, 0, o),     new Vec3(1 - o, o, o),     new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 0, 1 - o), sprite));
                quads.add(createQuad(new Vec3(o,     0, 1 - o), new Vec3(o,     o, 1 - o), new Vec3(o,     o, o),     new Vec3(o,     0, o), sprite));
                quads.add(createQuad(new Vec3(o,     o, o),     new Vec3(1 - o, o, o),     new Vec3(1 - o, 0, o),     new Vec3(o,     0, o), sprite));
                quads.add(createQuad(new Vec3(o,     0, 1 - o), new Vec3(1 - o, 0, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(o,     o, 1 - o), sprite));
            } else {
                quads.add(createQuad(new Vec3(o, o, o), new Vec3(1 - o, o, o), new Vec3(1 - o, o, 1 - o), new Vec3(o, o, 1 - o), sprite));
            }

            if (east) {
                quads.add(createQuad(new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1, 1 - o, 1 - o), new Vec3(1, 1 - o, o),     new Vec3(1 - o, 1 - o, o), sprite));
                quads.add(createQuad(new Vec3(1 - o, o,     o),     new Vec3(1, o,     o),     new Vec3(1, o,     1 - o), new Vec3(1 - o, o,     1 - o), sprite));
                quads.add(createQuad(new Vec3(1 - o, 1 - o, o),     new Vec3(1, 1 - o, o),     new Vec3(1, o,     o),     new Vec3(1 - o, o,     o), sprite));
                quads.add(createQuad(new Vec3(1 - o, o,     1 - o), new Vec3(1, o,     1 - o), new Vec3(1, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), sprite));
            } else {
                quads.add(createQuad(new Vec3(1 - o, o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, o, 1 - o), sprite));
            }

            if (west) {
                quads.add(createQuad(new Vec3(0, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o),     new Vec3(0, 1 - o, o), sprite));
                quads.add(createQuad(new Vec3(0, o,     o),     new Vec3(o, o,     o),     new Vec3(o, o,     1 - o), new Vec3(0, o,     1 - o), sprite));
                quads.add(createQuad(new Vec3(0, 1 - o, o),     new Vec3(o, 1 - o, o),     new Vec3(o, o,     o),     new Vec3(0, o,     o), sprite));
                quads.add(createQuad(new Vec3(0, o,     1 - o), new Vec3(o, o,     1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(0, 1 - o, 1 - o), sprite));
            } else {
                quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(o, 1 - o, 1 - o), new Vec3(o, 1 - o, o), new Vec3(o, o, o), sprite));
            }

            if (north) {
                quads.add(createQuad(new Vec3(o,     1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, 1 - o, 0), new Vec3(o,     1 - o, 0), sprite));
                quads.add(createQuad(new Vec3(o,     o,     0), new Vec3(1 - o, o,     0), new Vec3(1 - o, o,     o), new Vec3(o,     o,     o), sprite));
                quads.add(createQuad(new Vec3(1 - o, o,     0), new Vec3(1 - o, 1 - o, 0), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o,     o), sprite));
                quads.add(createQuad(new Vec3(o,     o,     o), new Vec3(o,     1 - o, o), new Vec3(o,     1 - o, 0), new Vec3(o,     o,     0), sprite));
            } else {
                quads.add(createQuad(new Vec3(o, 1 - o, o), new Vec3(1 - o, 1 - o, o), new Vec3(1 - o, o, o), new Vec3(o, o, o), sprite));
            }

            if (south) {
                quads.add(createQuad(new Vec3(o,     1 - o, 1),     new Vec3(1 - o, 1 - o, 1),     new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o,     1 - o, 1 - o), sprite));
                quads.add(createQuad(new Vec3(o,     o,     1 - o), new Vec3(1 - o, o,     1 - o), new Vec3(1 - o, o,     1),     new Vec3(o,     o,     1), sprite));
                quads.add(createQuad(new Vec3(1 - o, o,     1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(1 - o, 1 - o, 1),     new Vec3(1 - o, o,     1), sprite));
                quads.add(createQuad(new Vec3(o,     o,     1),     new Vec3(o,     1 - o, 1),     new Vec3(o,     1 - o, 1 - o), new Vec3(o,     o,     1 - o), sprite));
            } else {
                quads.add(createQuad(new Vec3(o, o, 1 - o), new Vec3(1 - o, o, 1 - o), new Vec3(1 - o, 1 - o, 1 - o), new Vec3(o, 1 - o, 1 - o), sprite));
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
            return XNetClientModelLoader.spriteCable;
        }

        @Override
        @SuppressWarnings("deprecation")
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }
    }
}
