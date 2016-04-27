package mcjty.xnet.client.model;

import mcjty.xnet.client.XNetClientModelLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static mcjty.xnet.cables.AbstractCableMultiPart.*;

@SideOnly(Side.CLIENT)
public class AdvancedCableISBM implements IBakedModel {

    private final boolean advanced;
    private final VertexFormat format;

    public static final ModelResourceLocation CABLE_MODEL = new ModelResourceLocation("xnet:netcable#multipart");
    public static final ModelResourceLocation ADVANCED_CABLE_MODEL = new ModelResourceLocation("xnet:advanced_netcable#multipart");

    public AdvancedCableISBM(boolean advanced, VertexFormat format) {
        this.advanced = advanced;
        this.format = format;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
        // our baked model implementation.
        System.out.println("Render");
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        boolean north = extendedBlockState.getValue(NORTH);
        boolean south = extendedBlockState.getValue(SOUTH);
        boolean west = extendedBlockState.getValue(WEST);
        boolean east = extendedBlockState.getValue(EAST);
        boolean up = extendedBlockState.getValue(UP);
        boolean down = extendedBlockState.getValue(DOWN);
        List<BakedQuad> quads = new ArrayList<>();
        double o = advanced ? .3 : .4;      // Thickness of the cable. .0 would be full block, .5 is infinitely thin.

        // For each side we either cap it off if there is no similar block adjacent on that side
        // or else we extend so that we touch the adjacent block:

        TextureAtlasSprite sprite = advanced ? XNetClientModelLoader.spriteAdvancedCable : XNetClientModelLoader.spriteCable;
        Function<SpriteIdx, TextureAtlasSprite> getSprite = advanced ? AdvancedCableISBM::getSpriteAdvanced : AdvancedCableISBM::getSpriteNormal;

        if (up) {
            quads.add(createQuad(new Vec3d(1 - o, 1,     o),     new Vec3d(1 - o, 1,     1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, o),     sprite));
            quads.add(createQuad(new Vec3d(o,     1,     1 - o), new Vec3d(o,     1,     o),     new Vec3d(o,     1 - o, o),     new Vec3d(o,     1 - o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o,     1,     o),     new Vec3d(1 - o, 1,     o),     new Vec3d(1 - o, 1 - o, o),     new Vec3d(o,     1 - o, o), sprite));
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1,     1 - o), new Vec3d(o,     1,     1 - o), sprite));
        } else {
            QuadSetting pattern = findPattern(west, south, east, north);
            quads.add(createQuad(new Vec3d(o,     1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, o),     new Vec3d(o,     1 - o, o), getSprite.apply(pattern.sprite), pattern.rotation));
        }

        if (down) {
            quads.add(createQuad(new Vec3d(1 - o, o, o),     new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 0, 1 - o), new Vec3d(1 - o, 0, o),     sprite));
            quads.add(createQuad(new Vec3d(o,     o, 1 - o), new Vec3d(o,     o, o),     new Vec3d(o,     0, o),     new Vec3d(o,     0, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o,     o, o),     new Vec3d(1 - o, o, o),     new Vec3d(1 - o, 0, o),     new Vec3d(o,     0, o), sprite));
            quads.add(createQuad(new Vec3d(o,     0, 1 - o), new Vec3d(1 - o, 0, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o,     o, 1 - o), sprite));
        } else {
            QuadSetting pattern = findPattern(west, north, east, south);
            quads.add(createQuad(new Vec3d(o, o, o), new Vec3d(1 - o, o, o), new Vec3d(1 - o, o, 1 - o), new Vec3d(o, o, 1 - o), getSprite.apply(pattern.sprite), pattern.rotation));
        }

        if (east) {
            quads.add(createQuad(new Vec3d(1, 1 - o, 1 - o), new Vec3d(1, 1 - o, o),     new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(1, o,     o),     new Vec3d(1, o,     1 - o), new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, o,     o),     sprite));
            quads.add(createQuad(new Vec3d(1, 1 - o, o),     new Vec3d(1, o,     o),     new Vec3d(1 - o, o,     o), new Vec3d(1 - o, 1 - o, o),     sprite));
            quads.add(createQuad(new Vec3d(1, o,     1 - o), new Vec3d(1, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, o,     1 - o), sprite));
        } else {
            QuadSetting pattern = findPattern(down, north, up, south);
            quads.add(createQuad(new Vec3d(1 - o, o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, o, 1 - o), getSprite.apply(pattern.sprite), pattern.rotation));
        }

        if (west) {
            quads.add(createQuad(new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o),     new Vec3d(0, 1 - o, o), new Vec3d(0, 1 - o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o, o,     o),     new Vec3d(o, o,     1 - o), new Vec3d(0, o,     1 - o), new Vec3d(0, o,     o),     sprite));
            quads.add(createQuad(new Vec3d(o, 1 - o, o),     new Vec3d(o, o,     o),     new Vec3d(0, o,     o), new Vec3d(0, 1 - o, o),     sprite));
            quads.add(createQuad(new Vec3d(o, o,     1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(0, 1 - o, 1 - o), new Vec3d(0, o,     1 - o), sprite));
        } else {
            QuadSetting pattern = findPattern(down, south, up, north);
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(o, 1 - o, 1 - o), new Vec3d(o, 1 - o, o), new Vec3d(o, o, o), getSprite.apply(pattern.sprite), pattern.rotation));
        }

        if (north) {
            quads.add(createQuad(new Vec3d(o,     1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, 1 - o, 0), new Vec3d(o,     1 - o, 0), sprite));
            quads.add(createQuad(new Vec3d(o,     o,     0), new Vec3d(1 - o, o,     0), new Vec3d(1 - o, o,     o), new Vec3d(o,     o,     o), sprite));
            quads.add(createQuad(new Vec3d(1 - o, o,     0), new Vec3d(1 - o, 1 - o, 0), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o,     o), sprite));
            quads.add(createQuad(new Vec3d(o,     o,     o), new Vec3d(o,     1 - o, o), new Vec3d(o,     1 - o, 0), new Vec3d(o,     o,     0), sprite));
        } else {
            QuadSetting pattern = findPattern(west, up, east, down);
            quads.add(createQuad(new Vec3d(o, 1 - o, o), new Vec3d(1 - o, 1 - o, o), new Vec3d(1 - o, o, o), new Vec3d(o, o, o), getSprite.apply(pattern.sprite), pattern.rotation));
        }

        if (south) {
            quads.add(createQuad(new Vec3d(o,     1 - o, 1),     new Vec3d(1 - o, 1 - o, 1),     new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o,     1 - o, 1 - o), sprite));
            quads.add(createQuad(new Vec3d(o,     o,     1 - o), new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, o,     1),     new Vec3d(o,     o,     1), sprite));
            quads.add(createQuad(new Vec3d(1 - o, o,     1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(1 - o, 1 - o, 1),     new Vec3d(1 - o, o,     1), sprite));
            quads.add(createQuad(new Vec3d(o,     o,     1),     new Vec3d(o,     1 - o, 1),     new Vec3d(o,     1 - o, 1 - o), new Vec3d(o,     o,     1 - o), sprite));
        } else {
            QuadSetting pattern = findPattern(west, down, east, up);
            quads.add(createQuad(new Vec3d(o, o, 1 - o), new Vec3d(1 - o, o, 1 - o), new Vec3d(1 - o, 1 - o, 1 - o), new Vec3d(o, 1 - o, 1 - o), getSprite.apply(pattern.sprite), pattern.rotation));
        }

        return quads;
    }

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite) {
        Vec3d normal = v1.subtract(v2).crossProduct(v3.subtract(v2));

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setTexture(sprite);
        putVertex(builder, normal, v1.xCoord, v1.yCoord, v1.zCoord, 0, 0, sprite);
        putVertex(builder, normal, v2.xCoord, v2.yCoord, v2.zCoord, 0, 16, sprite);
        putVertex(builder, normal, v3.xCoord, v3.yCoord, v3.zCoord, 16, 16, sprite);
        putVertex(builder, normal, v4.xCoord, v4.yCoord, v4.zCoord, 16, 0, sprite);
        return builder.build();
    }


    private void putVertex(UnpackedBakedQuad.Builder builder, Vec3d normal, double x, double y, double z, float u, float v, TextureAtlasSprite sprite) {
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

    private BakedQuad createQuad(Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4, TextureAtlasSprite sprite, int rotation) {
        switch (rotation) {
            case 0:
                return createQuad(v1, v2, v3, v4, sprite);
            case 1:
                return createQuad(v2, v3, v4, v1, sprite);
            case 2:
                return createQuad(v3, v4, v1, v2, sprite);
            case 3:
                return createQuad(v4, v1, v2, v3, sprite);
        }
        return createQuad(v1, v2, v3, v4, sprite);
    }

    public enum SpriteIdx {
        SPRITE_NONE,
        SPRITE_END,
        SPRITE_STRAIGHT,
        SPRITE_CORNER,
        SPRITE_THREE,
        SPRITE_CROSS
    }

    public static class QuadSetting {
        private final SpriteIdx sprite;
        private final int rotation;

        public QuadSetting(SpriteIdx sprite, int rotation) {
            this.rotation = rotation;
            this.sprite = sprite;
        }
    }

    public static class Pattern {
        private final boolean s1;
        private final boolean s2;
        private final boolean s3;
        private final boolean s4;

        public Pattern(boolean s1, boolean s2, boolean s3, boolean s4) {
            this.s1 = s1;
            this.s2 = s2;
            this.s3 = s3;
            this.s4 = s4;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Pattern pattern = (Pattern) o;

            return s1 == pattern.s1 && s2 == pattern.s2 && s3 == pattern.s3 && s4 == pattern.s4;

        }

        @Override
        public int hashCode() {
            int result = (s1 ? 1 : 0);
            result = 31 * result + (s2 ? 1 : 0);
            result = 31 * result + (s3 ? 1 : 0);
            result = 31 * result + (s4 ? 1 : 0);
            return result;
        }
    }

    private static final Map<Pattern, QuadSetting> PATTERNS = new HashMap<>();

    static {
        PATTERNS.put(new Pattern(false, false, false, false), new QuadSetting(SpriteIdx.SPRITE_NONE, 0));
        PATTERNS.put(new Pattern(true, false, false, false), new QuadSetting(SpriteIdx.SPRITE_END, 3));
        PATTERNS.put(new Pattern(false, true, false, false), new QuadSetting(SpriteIdx.SPRITE_END, 0));
        PATTERNS.put(new Pattern(false, false, true, false), new QuadSetting(SpriteIdx.SPRITE_END, 1));
        PATTERNS.put(new Pattern(false, false, false, true), new QuadSetting(SpriteIdx.SPRITE_END, 2));
        PATTERNS.put(new Pattern(true, true, false, false), new QuadSetting(SpriteIdx.SPRITE_CORNER, 0));
        PATTERNS.put(new Pattern(false, true, true, false), new QuadSetting(SpriteIdx.SPRITE_CORNER, 1));
        PATTERNS.put(new Pattern(false, false, true, true), new QuadSetting(SpriteIdx.SPRITE_CORNER, 2));
        PATTERNS.put(new Pattern(true, false, false, true), new QuadSetting(SpriteIdx.SPRITE_CORNER, 3));
        PATTERNS.put(new Pattern(false, true, false, true), new QuadSetting(SpriteIdx.SPRITE_STRAIGHT, 0));
        PATTERNS.put(new Pattern(true, false, true, false), new QuadSetting(SpriteIdx.SPRITE_STRAIGHT, 1));
        PATTERNS.put(new Pattern(true, true, true, false), new QuadSetting(SpriteIdx.SPRITE_THREE, 0));
        PATTERNS.put(new Pattern(false, true, true, true), new QuadSetting(SpriteIdx.SPRITE_THREE, 1));
        PATTERNS.put(new Pattern(true, false, true, true), new QuadSetting(SpriteIdx.SPRITE_THREE, 2));
        PATTERNS.put(new Pattern(true, true, false, true), new QuadSetting(SpriteIdx.SPRITE_THREE, 3));
        PATTERNS.put(new Pattern(true, true, true, true), new QuadSetting(SpriteIdx.SPRITE_CROSS, 0));
    }

    private static QuadSetting findPattern(boolean s1, boolean s2, boolean s3, boolean s4) {
        return PATTERNS.get(new Pattern(s1, s2, s3, s4));
    }

    private static TextureAtlasSprite getSpriteAdvanced(SpriteIdx idx) {
        switch (idx) {
            case SPRITE_NONE:
                return XNetClientModelLoader.spriteAdvancedNoneCable;
            case SPRITE_END:
                return XNetClientModelLoader.spriteAdvancedEndCable;
            case SPRITE_STRAIGHT:
                return XNetClientModelLoader.spriteAdvancedCable;
            case SPRITE_CORNER:
                return XNetClientModelLoader.spriteAdvancedCornerCable;
            case SPRITE_THREE:
                return XNetClientModelLoader.spriteAdvancedThreeCable;
            case SPRITE_CROSS:
                return XNetClientModelLoader.spriteAdvancedCrossCable;
        }
        return XNetClientModelLoader.spriteAdvancedNoneCable;
    }

    private static TextureAtlasSprite getSpriteNormal(SpriteIdx idx) {
        switch (idx) {
            case SPRITE_NONE:
                return XNetClientModelLoader.spriteNoneCable;
            case SPRITE_END:
                return XNetClientModelLoader.spriteEndCable;
            case SPRITE_STRAIGHT:
                return XNetClientModelLoader.spriteCable;
            case SPRITE_CORNER:
                return XNetClientModelLoader.spriteCornerCable;
            case SPRITE_THREE:
                return XNetClientModelLoader.spriteThreeCable;
            case SPRITE_CROSS:
                return XNetClientModelLoader.spriteCrossCable;
        }
        return XNetClientModelLoader.spriteNoneCable;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
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
        return XNetClientModelLoader.spriteAdvancedCable;
    }


    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        throw new UnsupportedOperationException();
    }
}
