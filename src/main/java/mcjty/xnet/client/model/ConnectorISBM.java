package mcjty.xnet.client.model;

import mcjty.xnet.client.XNetClientModelLoader;
import mcjty.xnet.connectors.AbstractConnectorPart;
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
import java.util.List;

@SideOnly(Side.CLIENT)
public class ConnectorISBM implements IBakedModel {

    private final TextureAtlasSprite spriteFace;
    private final VertexFormat format;

    public static final ModelResourceLocation RFCONNECTOR_MODEL = new ModelResourceLocation("xnet:rfconnector#multipart");
    public static final ModelResourceLocation ITEMCONNECTOR_MODEL = new ModelResourceLocation("xnet:itemconnector#multipart");

    public ConnectorISBM(TextureAtlasSprite spriteFace, VertexFormat format) {
        this.spriteFace = spriteFace;
        this.format = format;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing sidex, long rand) {
        // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
        // our baked model implementation.
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        EnumFacing side = extendedBlockState.getValue(AbstractConnectorPart.SIDE);
        List<BakedQuad> quads = new ArrayList<>();
        double p = .1;      // Thickness of the connector as it is put on the connecting block
        double q = .2;      // The wideness of the connector

        switch (side) {
            case DOWN:
                quads.add(createQuad(new Vec3d(1 - q, 0, q),     new Vec3d(1 - q, p, q),     new Vec3d(1 - q, p, 1 - q), new Vec3d(1 - q, 0, 1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     0, 1 - q), new Vec3d(q,     p, 1 - q), new Vec3d(q,     p, q),     new Vec3d(q,     0, q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     p, q),     new Vec3d(1 - q, p, q),     new Vec3d(1 - q, 0, q),     new Vec3d(q,     0, q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     0, 1 - q), new Vec3d(1 - q, 0, 1 - q), new Vec3d(1 - q, p, 1 - q), new Vec3d(q,     p, 1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     p, 1 - q), new Vec3d(1 - q, p, 1 - q), new Vec3d(1 - q, p, q),     new Vec3d(q,     p, q), spriteFace));
                break;
            case UP:
                quads.add(createQuad(new Vec3d(1 - q, 1 - p, q),     new Vec3d(1 - q, 1,     q),     new Vec3d(1 - q, 1,     1 - q), new Vec3d(1 - q, 1 - p, 1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     1 - p, 1 - q), new Vec3d(q,     1,     1 - q), new Vec3d(q,     1,     q),     new Vec3d(q,     1 - p, q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     1,     q),     new Vec3d(1 - q, 1,     q),     new Vec3d(1 - q, 1 - p, q),     new Vec3d(q,     1 - p, q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     1 - p, 1 - q), new Vec3d(1 - q, 1 - p, 1 - q), new Vec3d(1 - q, 1,     1 - q), new Vec3d(q,     1,     1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     1 - p, q),     new Vec3d(1 - q, 1 - p, q),     new Vec3d(1 - q, 1 - p, 1 - q), new Vec3d(q,     1 - p, 1 - q), spriteFace));
                break;
            case NORTH:
                quads.add(createQuad(new Vec3d(q,     1 - q, p), new Vec3d(1 - q, 1 - q, p), new Vec3d(1 - q, 1 - q, 0), new Vec3d(q,     1 - q, 0), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     q,     0), new Vec3d(1 - q, q,     0), new Vec3d(1 - q, q,     p), new Vec3d(q,     q,     p), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(1 - q, q,     0), new Vec3d(1 - q, 1 - q, 0), new Vec3d(1 - q, 1 - q, p), new Vec3d(1 - q, q,     p), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     q,     p), new Vec3d(q,     1 - q, p), new Vec3d(q,     1 - q, 0), new Vec3d(q,     q,     0), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q, q, p), new Vec3d(1 - q, q, p), new Vec3d(1 - q, 1 - q, p), new Vec3d(q, 1 - q, p), spriteFace));
                break;
            case SOUTH:
                quads.add(createQuad(new Vec3d(q,     1 - q, 1),     new Vec3d(1 - q, 1 - q, 1),     new Vec3d(1 - q, 1 - q, 1 - p), new Vec3d(q,     1 - q, 1 - p), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     q,     1 - p), new Vec3d(1 - q, q,     1 - p), new Vec3d(1 - q, q,     1),     new Vec3d(q,     q,     1), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(1 - q, q,     1 - p), new Vec3d(1 - q, 1 - q, 1 - p), new Vec3d(1 - q, 1 - q, 1),     new Vec3d(1 - q, q,     1), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q,     q,     1),     new Vec3d(q,     1 - q, 1),     new Vec3d(q,     1 - q, 1 - p), new Vec3d(q,     q,     1 - p), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(q, 1 - q, 1 - p), new Vec3d(1 - q, 1 - q, 1 - p), new Vec3d(1 - q, q, 1 - p), new Vec3d(q, q, 1 - p), spriteFace));
                break;
            case WEST:
                quads.add(createQuad(new Vec3d(0, 1 - q, 1 - q), new Vec3d(p, 1 - q, 1 - q), new Vec3d(p, 1 - q, q),     new Vec3d(0, 1 - q, q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(0, q,     q),     new Vec3d(p, q,     q),     new Vec3d(p, q,     1 - q), new Vec3d(0, q,     1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(0, 1 - q, q),     new Vec3d(p, 1 - q, q),     new Vec3d(p, q,     q),     new Vec3d(0, q,     q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(0, q,     1 - q), new Vec3d(p, q,     1 - q), new Vec3d(p, 1 - q, 1 - q), new Vec3d(0, 1 - q, 1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(p, q, q), new Vec3d(p, 1 - q, q), new Vec3d(p, 1 - q, 1 - q), new Vec3d(p, q, 1 - q), spriteFace));
                break;
            case EAST:
                quads.add(createQuad(new Vec3d(1 - p, 1 - q, 1 - q), new Vec3d(1, 1 - q, 1 - q), new Vec3d(1, 1 - q, q),     new Vec3d(1 - p, 1 - q, q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(1 - p, q,     q),     new Vec3d(1, q,     q),     new Vec3d(1, q,     1 - q), new Vec3d(1 - p, q,     1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(1 - p, 1 - q, q),     new Vec3d(1, 1 - q, q),     new Vec3d(1, q,     q),     new Vec3d(1 - p, q,     q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(1 - p, q,     1 - q), new Vec3d(1, q,     1 - q), new Vec3d(1, 1 - q, 1 - q), new Vec3d(1 - p, 1 - q, 1 - q), XNetClientModelLoader.spriteSide));
                quads.add(createQuad(new Vec3d(1 - p, q, 1 - q), new Vec3d(1 - p, 1 - q, 1 - q), new Vec3d(1 - p, 1 - q, q), new Vec3d(1 - p, q, q), spriteFace));
                break;
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
        return XNetClientModelLoader.spriteSide;
    }


    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        throw new UnsupportedOperationException();
    }
}
