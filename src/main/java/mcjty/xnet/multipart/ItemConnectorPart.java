package mcjty.xnet.multipart;

import net.minecraft.util.EnumFacing;

public class ItemConnectorPart extends AbstractConnectorPart {

    public ItemConnectorPart(EnumFacing side) {
        super(side);
    }

    public ItemConnectorPart(){
        super();
    }

    @Override
    public String getModelPath() {
        return "xnet:connector";
    }
}
