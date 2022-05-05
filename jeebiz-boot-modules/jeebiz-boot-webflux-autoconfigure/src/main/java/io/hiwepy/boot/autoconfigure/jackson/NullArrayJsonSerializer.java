package io.hiwepy.boot.autoconfigure.jackson;

import java.io.IOException;

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
		jsonGenerator.writeStartArray();
		jsonGenerator.writeEndArray();
	}
	
}
