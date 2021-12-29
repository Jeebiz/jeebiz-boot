package net.jeebiz.boot.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hitool.core.lang3.time.DateFormats;
import net.jeebiz.boot.autoconfigure.jackson.MyBeanSerializerModifier;

/**
 * 
 * https://www.cnblogs.com/BlogNetSpace/p/15166552.html
 */
@Configuration(proxyBeanMethods = false)
public class DefaultMessageConvertersConfiguration {

	@Bean
	@Primary
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
			ObjectProvider<ObjectMapper> objectMapperProvider) {
		ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(() -> {
			
			ObjectMapper objectMapperDef = Jackson2ObjectMapperBuilder.json()
					.simpleDateFormat(DateFormats.DATE_LONGFORMAT).failOnEmptyBeans(false)
					.failOnUnknownProperties(false)
					.featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
					.serializationInclusion(JsonInclude.Include.NON_NULL).build();

			/** 为objectMapper注册一个带有SerializerModifier的Factory */
			objectMapperDef.setSerializerFactory(objectMapperDef.getSerializerFactory().withSerializerModifier(new MyBeanSerializerModifier()));
			return objectMapperDef;
		});
		return new MappingJackson2HttpMessageConverter(objectMapper);
	}

}
