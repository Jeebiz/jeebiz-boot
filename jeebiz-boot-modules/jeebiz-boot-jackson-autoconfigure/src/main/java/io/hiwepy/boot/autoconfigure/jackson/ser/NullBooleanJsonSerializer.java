package io.hiwepy.boot.autoconfigure.jackson.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

/**
 * 处理boolean类型的null值
 */
public class NullBooleanJsonSerializer extends JsonSerializer<Object> {

	public static final NullBooleanJsonSerializer INSTANCE = new NullBooleanJsonSerializer();
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (Objects.isNull(value)) {
			jsonGenerator.writeBoolean(Boolean.FALSE);
		}
	}
	
}