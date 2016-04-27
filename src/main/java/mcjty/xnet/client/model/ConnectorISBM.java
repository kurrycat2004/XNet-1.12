package mcjty.xnet.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import mcjty.xnet.client.XNetClientModelLoader;
import mcjty.xnet.varia.CommonProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

import static mcjty.xnet.client.XNetClientModelLoader.quadBakery;

@SideOnly(Side.CLIENT)
public class ConnectorISBM implements IBakedModel {

    public ConnectorISBM(IConnectorRenderable renderObject) {
        this.spriteFront = renderObject.getTexture(true);
        this.spriteBack = renderObject.getTexture(false);
        this.front = renderObject.renderFront();
    }

    private final TextureAtlasSprite spriteFront, spriteBack;
    private final boolean front;
    private static final float p = 1.6f; // (.1 * 16)      Thickness of the connector as it is put on the connecting block
    private static final float q = 3.2f; // (.2 * 16)      The wideness of the connector

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing facing, long rand) {
        // Called with the blockstate from our block. Here we get the values of the six properties and pass that to
        // our baked model implementation.
        if (facing != null){
            return ImmutableList.of();
        }
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
        EnumFacing side = extendedBlockState.getValue(CommonProperties.FACING_PROPERTY);
        List<BakedQuad> quads = Lists.newArrayList();
        createQuads(quads, side);
        return quads;
    }

    private void createQuads(List<BakedQuad> quads, EnumFacing facing){
        ModelRotation rot = RenderHelper.defaultFor(facing);
        quads.add(quadBakery.bakeQuad(new Vector3f(16 - q, 16 - q,  p), new Vector3f(q, q, p), spriteBack, EnumFacing.SOUTH, rot));
        if (front){
            quads.add(quadBakery.bakeQuad(new Vector3f(16 - q, q,  0), new Vector3f(q, 16 - q, 0), spriteFront, EnumFacing.SOUTH, rot));
        }
        for (ITransformation mr : RenderHelper.getTranformationsFor(facing)){
            quads.add(quadBakery.bakeQuad(new Vector3f(16 - q, 16-q, p), new Vector3f(q, 16-q, 0), XNetClientModelLoader.spriteSide, EnumFacing.UP, mr));
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
