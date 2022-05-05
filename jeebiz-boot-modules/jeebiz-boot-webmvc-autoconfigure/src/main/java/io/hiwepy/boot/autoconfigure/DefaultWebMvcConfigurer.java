/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import hitool.core.lang3.time.DateFormats;
import io.hiwepy.boot.api.web.servlet.handler.Slf4jMDCInterceptor;
import io.hiwepy.boot.autoconfigure.config.LocalResourceProperteis;
import io.hiwepy.boot.autoconfigure.jackson.MyBeanSerializerModifier;

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

	/**
	 * https://blog.csdn.net/litte_frog/article/details/82764215
	 ByteArrayHttpMessageConverter – converts byte arrays
	 StringHttpMessageConverter – converts Strings
	 ResourceHttpMessageConverter – converts org.springframework.core.io.Resource for any type of octet stream
	 SourceHttpMessageConverter – converts javax.xml.transform.Source
	 FormHttpMessageConverter – converts form data to/from a MultiValueMap<String, String>.
	 Jaxb2RootElementHttpMessageConverter – converts Java objects to/from XML (added only if JAXB2 is present on the classpath)
	 MappingJackson2HttpMessageConverter – converts JSON (added only if Jackson 2 is present on the classpath)
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		// 单独初始化ObjectMapper，不使用全局对象，因为下面要指定特殊的输出处理，会影响内部业务逻辑
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
				.simpleDateFormat(DateFormats.DATE_LONGFORMAT)
				.failOnEmptyBeans(false)
				.failOnUnknownProperties(false)
				.featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS).build();

		/** 为objectMapper注册一个带有SerializerModifier的Factory */
		objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(new MyBeanSerializerModifier()));

		//SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
		//serializerProvider.setNullValueSerializer(NullObjectJsonSerializer.INSTANCE);
		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		converters.add(new ResourceHttpMessageConverter());
		converters.add(new ResourceRegionHttpMessageConverter());
		try {
			converters.add(new SourceHttpMessageConverter<>());
		}
		catch (Throwable ex) {
			// Ignore when no TransformerFactory implementation is available...
		}
		converters.add(new AllEncompassingFormHttpMessageConverter());
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

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
