/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.sample.setup.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.extra.converters.BooleanStringConverter;
import com.github.dozermapper.extra.converters.JSONArrayStringConverter;
import com.github.dozermapper.extra.converters.JSONObjectStringConverter;
import com.github.dozermapper.extra.converters.number.BigDecimalStringConverter;
import com.github.dozermapper.extra.converters.number.BigIntegerStringConverter;
import com.github.dozermapper.spring.DozerBeanMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerMapperConfiguration implements DozerBeanMapperBuilderCustomizer {

	@Bean
	public BooleanStringConverter booleanStringConverter() {
		return new BooleanStringConverter();
	}
	
	@Bean
	public BigDecimalStringConverter bigDecimalStringConverter() {
		return new BigDecimalStringConverter();
	}
	
	@Bean
	public BigIntegerStringConverter bigIntegerStringConverter() {
		return new BigIntegerStringConverter();
	}
	
	@Bean
	public JSONArrayStringConverter jsonArrayStringConverter() {
		return new JSONArrayStringConverter();
	}
	
	@Bean
	public JSONObjectStringConverter jsonObjectStringConverter() {
		return new JSONObjectStringConverter();
	}

	@Override
	public void customize(DozerBeanMapperBuilder builder) {
		builder.withCustomConverters(jsonArrayStringConverter(), jsonObjectStringConverter(), bigDecimalStringConverter(), bigDecimalStringConverter());
		
	}
	
}
