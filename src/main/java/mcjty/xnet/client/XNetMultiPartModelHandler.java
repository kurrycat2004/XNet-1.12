package mcjty.xnet.client;

import elec332.core.client.newstuff.IMultipartModelHandler;
import elec332.core.client.newstuff.ModelHandler;
import mcjty.xnet.XNet;
import mcjty.xnet.cables.AbstractCableMultiPart;
import mcjty.xnet.client.model.AdvancedCableISBM;
import mcjty.xnet.client.model.ConnectorISBM;
import mcjty.xnet.client.model.IConnectorRenderable;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 27-4-2016.
 */
@ModelHandler
@SideOnly(Side.CLIENT)
public class XNetMultiPartModelHandler implements IMultipartModelHandler {

    @Override
    public boolean handlePart(IMultipart multipart) {
        return MultipartRegistry.getPartType(multipart).getResourceDomain().equals(XNet.MODID);
    }

    @Override
    public String getIdentifier(IBlockState state, IMultipart multipart) {
        return "multipart";
    }

    @Override
    public IBakedModel getModelFor(IMultipart part, IBlockState state, String identifier, ModelResourceLocation fullResourceLocation) {
        if (part instanceof IConnectorRenderable) {
            return new ConnectorISBM((IConnectorRenderable) part);
        }
        if (part instanceof AbstractCableMultiPart){
            return new AdvancedCableISBM(((AbstractCableMultiPart) part).isAdvanced(), DefaultVertexFormats.ITEM);
        }
        throw new IllegalArgumentException();
    }

}
