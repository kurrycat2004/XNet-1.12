package mcjty.xnet.api.ideas;

/**
 * Created by Elec332 on 2-3-2016.
 */
public interface IXNetChannel<C> {

    /**
     * Called when a compatible object is added to the network
     *
     * @param object The object
     */
    public void addObject(C object);

    /**
     * Called when a object is removed to the network
     *
     * @param object The object
     */
    public void removeObject(C object);

    /**
     * Used to check if a certain object is compatible with this network.
     *
     * @param object The object
     * @return Whether the object is compatible with this network.
     */
    public boolean isValidObject(C object);

    /**
     * Gets called every tick
     */
    public void tick();

    /**
     * Called just before the channel gets removed
     */
    public void invalidate();

    public interface Factory<C> {

        public IXNetChannel<C> createNewChannel(C firstConnector);

    }

}
