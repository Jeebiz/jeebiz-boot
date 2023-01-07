package io.hiwepy.boot.autoconfigure.jackson.ser;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 处理数组集合类型的null值
 */
public class NullArrayJsonSerializer extends JsonSerializer<Object> {
	
	public static final NullArrayJsonSerializer INSTANCE = new NullArrayJsonSerializer();
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (Objects.isNull(value)) {
			jsonGenerator.writeStartArray();
			jsonGenerator.writeEndArray();
		}
	}
	
}