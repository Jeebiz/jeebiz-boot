package io.hiwepy.boot.autoconfigure.jackson.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 处理日期类型的null值
 */
public class NullDateJsonSerializer extends JsonSerializer<Object> {

    public static final NullDateJsonSerializer INSTANCE = new NullDateJsonSerializer();

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (Objects.isNull(value)) {
            jsonGenerator.writeString(StringUtils.EMPTY);
        }
    }

}
