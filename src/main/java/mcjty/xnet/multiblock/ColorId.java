package mcjty.xnet.multiblock;

/**
 * Every cable has a 'color' associated with it. Cables with different color will
 * never mix with cables of another color
 */
public class ColorId {

    private final int id;

    public ColorId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorId blobId = (ColorId) o;

        if (id != blobId.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
