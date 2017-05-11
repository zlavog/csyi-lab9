package amazon.cloudshape.lab9.queue;


import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.util.Objects;

public class QueueMessage<T> {
    /**
     * The actual data stored in the queue.
     */
    private final T payload;

    /**
     * The payload identifier. Specific to each queue service.
     */
    private final String idForDelete;

    /**
     * Constructs a new payload.
     *
     * @param payload the payload
     * @param idForDelete the idForDelete
     */
    public QueueMessage(T payload, String idForDelete) {
        Preconditions.checkNotNull(payload, "The payload must not be null.");
        Preconditions.checkNotNull(idForDelete, "The idForDelete must not be null.");

        this.payload = payload;
        this.idForDelete = idForDelete;
    }

    public T getPayload() {
        return payload;
    }

    public String getIdForDelete() {
        return idForDelete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idForDelete, payload);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final QueueMessage other = (QueueMessage)obj;
        return Objects.equals(this.idForDelete, other.idForDelete)
                && Objects.equals(this.payload, other.payload);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("payload", payload)
                .add("idForDelete", idForDelete)
                .toString();
    }
}
