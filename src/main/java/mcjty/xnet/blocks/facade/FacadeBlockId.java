package mcjty.xnet.blocks.facade;

public class FacadeBlockId {
    private final String registryName;
    private final int meta;

    public FacadeBlockId(String registryName, int meta) {
        this.registryName = registryName;
        this.meta = meta;
    }

    public String getRegistryName() {
        return registryName;
    }

    public int getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return registryName + '@' + meta;
    }
}
