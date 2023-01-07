package io.hiwepy.boot.autoconfigure.jackson.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

/**
 *
 */
public class NullJsonSerializer extends JsonSerializer<Object> {

	public static final NullJsonSerializer INSTANCE = new NullJsonSerializer();
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (Objects.isNull(value)) {
			jsonGenerator.writeNull();
		}
	}
	
}
