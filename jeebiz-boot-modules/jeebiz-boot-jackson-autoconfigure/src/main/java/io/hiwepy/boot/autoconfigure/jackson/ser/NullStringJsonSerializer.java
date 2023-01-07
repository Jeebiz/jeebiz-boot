package io.hiwepy.boot.autoconfigure.jackson.ser;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

/**
 * 处理字符串类型的null值
 */
public class NullStringJsonSerializer extends JsonSerializer<Object> {

	public static final NullStringJsonSerializer INSTANCE = new NullStringJsonSerializer();
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		if (Objects.isNull(value)) {
			jsonGenerator.writeString(StringUtils.EMPTY);
		}
	}
	
}
