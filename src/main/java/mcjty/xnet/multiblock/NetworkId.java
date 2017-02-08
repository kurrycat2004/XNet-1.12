package mcjty.xnet.multiblock;

public class NetworkId {

    private final int id;

    public NetworkId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkId blobId = (NetworkId) o;

        if (id != blobId.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
