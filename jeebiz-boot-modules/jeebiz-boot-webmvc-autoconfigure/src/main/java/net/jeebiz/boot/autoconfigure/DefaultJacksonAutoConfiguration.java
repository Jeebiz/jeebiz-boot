package net.jeebiz.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hitool.core.lang3.time.DateFormats;
import net.jeebiz.boot.autoconfigure.jackson.MyBeanSerializerModifier;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ObjectMapper.class, Jackson2ObjectMapperBuilder.class })
@AutoConfigureBefore({JacksonAutoConfiguration.class, DefaultMessageConvertersConfiguration.class})
public class DefaultJacksonAutoConfiguration {

	@Bean
	@Order(Integer.MIN_VALUE)
	@Primary
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return Jackson2ObjectMapperBuilder.json()
				.simpleDateFormat(DateFormats.DATE_LONGFORMAT)
				.failOnEmptyBeans(false).failOnUnknownProperties(false)
				.featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
				.serializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	@Bean
	@Order(Integer.MIN_VALUE)
	@Primary
	public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
		return new Jackson2ObjectMapperBuilderCustomizer() {

			@Override
			public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {

				jacksonObjectMapperBuilder
					.simpleDateFormat(DateFormats.DATE_LONGFORMAT)	
					.failOnEmptyBeans(false)
					.failOnUnknownProperties(false)
					.featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
					.serializationInclusion(JsonInclude.Include.NON_NULL);
				
			}

		};
	}

	@Bean
	@Order(Integer.MIN_VALUE)
	@Primary
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		
		/** 为objectMapper注册一个带有SerializerModifier的Factory */
		objectMapper.setSerializerFactory(
				objectMapper.getSerializerFactory().withSerializerModifier(new MyBeanSerializerModifier()));
		
		return objectMapper;
	}
	
}
