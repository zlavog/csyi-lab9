package amazon.cloudshape.lab9.serialization;

import amazon.cloudshape.lab9.model.CatInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PayloadConverterImplTest {

    @Test
    public void testSerializingCats() throws Exception {
        PayloadConverterImpl<CatInfo> converter = new PayloadConverterImpl<>();
        converter.setClazz(CatInfo.class);

        CatInfo catInfo = new CatInfo()
                .withName("a cat's name")
                .withAge(7)
                .withDescription("some description");

        String serializedCat = converter.serialize(catInfo);
        CatInfo deserializedCat = converter.deserialize(serializedCat);

        assertEquals(catInfo, deserializedCat);
    }
}