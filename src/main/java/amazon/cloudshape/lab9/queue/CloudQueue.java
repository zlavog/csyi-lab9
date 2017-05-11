package amazon.cloudshape.lab9.queue;


import java.util.List;

public interface CloudQueue<T> {

    /**
     * Peek the next item from the queue.
     *
     * @return the next item or null if no item is immediately available
     */
    QueueMessage<T> peek();

    /**
     * Pulls the next items from the queue, but does not remove them.
     *
     * @param maxOutputSize the maximum number of items to pull; between 1 and 10.
     * @return the next item list or an empty list if no item is immediately available.
     */
    List<QueueMessage<T>> peek(int maxOutputSize);

    /**
     * Deletes the specified item from the queue.
     *
     * @param id the ID of item to be deleted from the queue
     */
    void delete(String id);

    /**
     * Deletes the specified items from the queue.
     *
     * @param ids the ID of items to be deleted from the queue
     */
    void delete(List<String> ids);

    /**
     * Put the specified item into the queue.
     *
     * @param item the item to be put into the queue
     */
    void put(T item);
}
