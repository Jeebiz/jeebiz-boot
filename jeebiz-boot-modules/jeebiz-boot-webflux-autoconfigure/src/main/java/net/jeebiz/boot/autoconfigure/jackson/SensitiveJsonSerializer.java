package net.jeebiz.boot.autoconfigure.jackson;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * @author felord.cn
 * @since 1.0.8.RELEASE
 * https://mp.weixin.qq.com/s/GmELzTYIwYAIpTVRyCh9mw
 */
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private SensitiveStrategy strategy;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
           gen.writeString(strategy.desensitizer().apply(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {

            Sensitive annotation = property.getAnnotation(Sensitive.class);
            if (Objects.nonNull(annotation)&&Objects.equals(String.class, property.getType().getRawClass())) {
                this.strategy = annotation.strategy();
                return this;
            }
            return prov.findValueSerializer(property.getType(), property);

    }
}