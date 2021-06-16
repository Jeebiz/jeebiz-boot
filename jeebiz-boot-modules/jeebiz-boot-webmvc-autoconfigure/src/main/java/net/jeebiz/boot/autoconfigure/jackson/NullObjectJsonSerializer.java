package net.jeebiz.boot.autoconfigure.jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 处理实体对象类型的null值
 */
public class NullObjectJsonSerializer extends JsonSerializer<Object> {

	public static final NullObjectJsonSerializer INSTANCE = new NullObjectJsonSerializer();
	private static final Map<String, Object> EMPTY_MAP = new HashMap<>(1);
	
	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		
		//JsonStreamContext outputContext = jsonGenerator.getOutputContext();
		//Object currentValue = outputContext.getCurrentValue();// 这里可以获取到被序列化的对象
		//String currentName = outputContext.getCurrentName(); // 这里获取了序列化的属性
		// 一个被序列化的对象找到了，这个当前序列化的属性也找到了，所以如果借用反射方式可以获取当前的类型
		//if (Objects.nonNull(currentValue)) {
		//	Field findField = ReflectionUtils.findField(currentValue.getClass(), currentName);
		//	Class<?> filedType = findField.getType(); // 获取字段的类型 // 这里开始写入数据
			//jsonGenerator.writeObject("");
		//}
		jsonGenerator.writeObject(EMPTY_MAP);
	}
	
}
