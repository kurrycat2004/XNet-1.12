package mcjty.xnet.multiblock;

public class BlobId {

    private final int id;

    public BlobId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlobId blobId = (BlobId) o;

        if (id != blobId.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
