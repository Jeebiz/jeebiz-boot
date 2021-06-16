package net.jeebiz.boot.autoconfigure.jackson;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * <pre>
 * 此modifier主要做的事情为：
 * 1.当序列化类型为数组集合时，当值为null时，序列化成[]
 * 2.String类型值序列化为""
 *
 * </pre>
 */
public class MyBeanSerializerModifier extends BeanSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, 
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        // 1、循环所有的beanPropertyWriter
        for (BeanPropertyWriter writer : beanProperties) {
        	
        	// 2、当前属性的Java类型
        	Class<?> rawClass = writer.getType().getRawClass();
            // 3、判断字段的类型，如果是array，list，set则注册nullSerializer
            if (this.isArrayType(rawClass)) {
                writer.assignNullSerializer(NullArrayJsonSerializer.INSTANCE);
            } else if (this.isNumberType(rawClass)) {
                writer.assignNullSerializer(NullNumberJsonSerializer.INSTANCE);
            } else if (this.isBooleanType(rawClass)) {
                writer.assignNullSerializer(NullBooleanJsonSerializer.INSTANCE);
            } else if (this.isStringType(rawClass)) {
                writer.assignNullSerializer(NullStringJsonSerializer.INSTANCE);
            } else {
                writer.assignNullSerializer(NullObjectJsonSerializer.INSTANCE);
            }
        }
        return beanProperties;
    }

    /**
     * 1、是否是数组
     */
    protected boolean isArrayType(Class<?> clazz) {
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 2、是否是String
     */
    protected boolean isStringType(Class<?> clazz) {
        return CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz);
    }

    /**
     * 3、是否是数值类型
     */
    protected boolean isNumberType(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

    /**
     * 4、是否是boolean
     */
	protected boolean isBooleanType(Class<?> clazz) {
        return clazz.equals(Boolean.class);
    }

}