package amazon.cloudshape.lab9.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class PayloadConverterImpl<T> implements PayloadConverter<T> {

    private ObjectMapper mapper;
    private Class<T> clazz;

    public PayloadConverterImpl() {
        this(new ObjectMapper());
    }

    public PayloadConverterImpl(ObjectMapper mapper) {
        this.mapper = mapper;

        this.mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        this.mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);

        this.mapper.registerModule(new SimpleModule("CloudShapeYourIdea", new Version(1, 0, 0, null, "fii", "1.0")));
    }

    @Override
    public String serialize(T value) throws ItemSerializationException {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new ItemSerializationException(ex);
        }
    }

    @Override
    public T deserialize(String content) {
        try {
            return mapper.readValue(content, clazz);
        } catch (Exception ex) {
            throw new ItemSerializationException(ex);
        }
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }
}
