/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.autoconfigure;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import hitool.core.lang3.time.DateFormats;
import net.jeebiz.boot.api.web.servlet.handler.Slf4jMDCInterceptor;
import net.jeebiz.boot.autoconfigure.config.LocalResourceProperteis;
import net.jeebiz.boot.autoconfigure.jackson.MyBeanSerializerModifier;

public class DefaultWebMvcConfigurer implements WebMvcConfigurer {

	private final String META_INF_RESOURCES = "classpath:/META-INF/resources/";
	private final String META_INF_WEBJAR_RESOURCES = META_INF_RESOURCES + "webjars/";

	private ThemeChangeInterceptor themeChangeInterceptor;
	private LocaleChangeInterceptor localeChangeInterceptor;
	private Slf4jMDCInterceptor slf4jMDCInterceptor;
    private LocalResourceProperteis localResourceProperteis;

    public DefaultWebMvcConfigurer(LocalResourceProperteis localResourceProperteis,
			ThemeChangeInterceptor themeChangeInterceptor, LocaleChangeInterceptor localeChangeInterceptor,
			Slf4jMDCInterceptor slf4jMDCInterceptor) {
		super();
		this.localResourceProperteis = localResourceProperteis;
		this.themeChangeInterceptor = themeChangeInterceptor;
		this.localeChangeInterceptor = localeChangeInterceptor;
		this.slf4jMDCInterceptor = slf4jMDCInterceptor;
	}

    @Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

    @Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    	ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

        /** 为objectMapper注册一个带有SerializerModifier的Factory */
		objectMapper.setSerializerFactory(objectMapper.getSerializerFactory()
				.withSerializerModifier(new MyBeanSerializerModifier()))
				.enable(MapperFeature.USE_GETTERS_AS_SETTERS)
				.enable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.setDateFormat(DateFormats.getDateFormat(DateFormats.DATE_LONGFORMAT));

        //SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        //serializerProvider.setNullValueSerializer(NullObjectJsonSerializer.INSTANCE);

		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
	}

    @Override
	public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(slf4jMDCInterceptor).addPathPatterns("/**").order(Integer.MIN_VALUE);
		registry.addInterceptor(themeChangeInterceptor).addPathPatterns("/**").order(Integer.MIN_VALUE + 1);
		registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/**").order(Integer.MIN_VALUE + 2);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	// 本地资源映射
    	if(!CollectionUtils.isEmpty(localResourceProperteis.getLocalLocations())){
    		Iterator<Entry<String, String>> ite = localResourceProperteis.getLocalLocations().entrySet().iterator();
    		while (ite.hasNext()) {
				Entry<String, String> entry = ite.next();
				if (localResourceProperteis.isLocalRelative()) {
					registry.addResourceHandler(entry.getKey()).addResourceLocations(ResourceUtils.FILE_URL_PREFIX
							+ localResourceProperteis.getLocalStorage() + File.separator + entry.getValue());
				} else {
					registry.addResourceHandler(entry.getKey()).addResourceLocations(entry.getValue());
				}
			}
		}
    	// 指定个性化资源映射
    	registry.addResourceHandler("/assets/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/assets/");
    	registry.addResourceHandler("/js/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/js/");
    	registry.addResourceHandler("/css/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/css/");
		registry.addResourceHandler("/images/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/images/");
		if(!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations(META_INF_WEBJAR_RESOURCES)
				.resourceChain(false).addResolver(new WebJarsResourceResolver());
		}

	}

}
