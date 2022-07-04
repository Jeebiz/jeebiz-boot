package io.hiwepy.boot.autoconfigure.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 处理数值类型的null值
 */
public class NullNumberJsonSerializer extends JsonSerializer<Object> {

	public static final NullNumberJsonSerializer INSTANCE = new NullNumberJsonSerializer();
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		jsonGenerator.writeNumber(0);
	}
	
}
