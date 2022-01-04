package net.jeebiz.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hitool.core.lang3.time.DateFormats;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ObjectMapper.class, Jackson2ObjectMapperBuilder.class })
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class DefaultJacksonAutoConfiguration {

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
	@Order(Integer.MIN_VALUE)
	@Primary
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		return builder.createXmlMapper(false).build();
	}

}
