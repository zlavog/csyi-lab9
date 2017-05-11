package amazon.cloudshape.lab9.serialization;

public interface PayloadConverter<T> {

    /**
     * Implements conversion of the single value.
     *
     * @param value
     *            Java value to convert to String.
     * @return converted value
     */
    String serialize(T value) throws ItemSerializationException;

    /**
     * Implements conversion of the single value.
     *
     * @param content
     *            Simple Workflow Data value to convert to a Java object.
     * @return converted Java object
     */
    <T> T deserialize(String content) throws ItemSerializationException;
}
