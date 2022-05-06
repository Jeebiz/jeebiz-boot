package io.hiwepy.boot.autoconfigure;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hitool.core.lang3.time.DateFormats;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ObjectMapper.class, Jackson2ObjectMapperBuilder.class })
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class DefaultJacksonAutoConfiguration {

	@Value("${spring.jackson.date-format::#{null}}")
	private String pattern;

	@Bean
	@Order(Integer.MIN_VALUE)
	@Primary
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return Jackson2ObjectMapperBuilder.json()
				.simpleDateFormat(DateFormats.DATE_LONGFORMAT)
				.failOnEmptyBeans(false)
				.failOnUnknownProperties(false)
				.featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS);
	}

	@Bean
	@Primary
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		Map<Class<?>, JsonSerializer<?>> map = new HashMap<>();
		if(StringUtils.hasText(pattern)){
			map.put(LocalDateTime.class, localDateTimeSerializer());
		}
		map.put(Long.class, ToStringSerializer.instance);
		return builder -> builder.serializersByType(map);
	}

	public LocalDateTimeSerializer localDateTimeSerializer() {
		return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
	}

	@Bean
	@Order(Integer.MIN_VALUE)
	@Primary
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		return builder.createXmlMapper(false).build();
	}

}
