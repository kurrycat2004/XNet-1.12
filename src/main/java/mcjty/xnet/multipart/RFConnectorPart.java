package mcjty.xnet.multipart;

import net.minecraft.util.EnumFacing;

public class RFConnectorPart extends AbstractConnectorPart {

    public RFConnectorPart(EnumFacing side) {
        super(side);
    }

    public RFConnectorPart(){
        super();
    }

    @Override
    public String getModelPath() {
        return "xnet:connector";
    }
}
