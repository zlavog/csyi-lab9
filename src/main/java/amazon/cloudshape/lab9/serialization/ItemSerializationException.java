package amazon.cloudshape.lab9.serialization;


import com.google.common.base.Strings;

/**
 * Thrown to indicate that an exception occurred during
 * item serialization/deserialization in/out of the queue.
 */
public class ItemSerializationException extends RuntimeException {

    /**
     * <p>The ID of the item.<p/>
     * The item ID is available only if the exception occurred at a point where
     * an ID has actually been assigned to the queue item. Otherwise, item ID is null.
     */
    private String id = null;


    public ItemSerializationException() {
    }


    public ItemSerializationException(String message) {
        super(message);
    }


    public ItemSerializationException(Throwable cause) {
        initCause(cause);
    }


    public ItemSerializationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }


    public ItemSerializationException(Throwable cause, String id) {
        initCause(cause);
        setId(id);
    }


    public ItemSerializationException(String message, Throwable cause, String id) {
        super(message);
        initCause(cause);
        setId(id);
    }


    /**
     * Returns the ID of the item.
     *
     * @return the ID of the item
     */
    public String getId() {
        return id;
    }


    /**
     * Sets the ID of the item.
     *
     * @param id the ID of the item
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(id)) {
            return super.toString();
        } else {
            return String.format("%s: queue item %s", super.toString(), id);
        }
    }
}
