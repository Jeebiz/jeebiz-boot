package net.jeebiz.boot.autoconfigure.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 处理字符串类型的null值
 */
public class NullStringJsonSerializer extends JsonSerializer<Object> {

	public static final NullStringJsonSerializer INSTANCE = new NullStringJsonSerializer();
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		jsonGenerator.writeString("");
	}
	
}
