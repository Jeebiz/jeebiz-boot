/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import hitool.core.lang3.time.DateFormats;
import io.hiwepy.boot.api.MediaTypes;
import io.hiwepy.boot.api.web.servlet.handler.Slf4jMDCInterceptor;
import io.hiwepy.boot.autoconfigure.config.LocalResourceProperteis;
import io.hiwepy.boot.autoconfigure.jackson.ser.MyBeanSerializerModifier;
import org.springframework.http.MediaType;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
     * ByteArrayHttpMessageConverter – converts byte arrays
     * StringHttpMessageConverter – converts Strings
     * ResourceHttpMessageConverter – converts org.springframework.core.io.Resource for any type of octet stream
     * SourceHttpMessageConverter – converts javax.xml.transform.Source
     * FormHttpMessageConverter – converts form data to/from a MultiValueMap<String, String>.
     * Jaxb2RootElementHttpMessageConverter – converts Java objects to/from XML (added only if JAXB2 is present on the classpath)
     * MappingJackson2HttpMessageConverter – converts JSON (added only if Jackson 2 is present on the classpath)
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        // 指定BigDecimal类型字段使用自定义的CustomDoubleSerialize序列化器
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateFormats.DATE_LONGFORMAT);
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DateFormats.DATE_FORMAT);
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormat));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormat));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DateFormats.TIME_FORMAT);
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        simpleModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                if (p == null) {
                    return null;
                }
                JsonNode node = p.getCodec().readTree(p);
                if (node == null || node.asText() == null) {
                    return null;
                }
                return DateUtil.parse(node.asText());
            }
        });

        // 单独初始化ObjectMapper，不使用全局对象，因为下面要指定特殊的输出处理，会影响内部业务逻辑
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(simpleModule, new JavaTimeModule())
                // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE));
                .simpleDateFormat(DateFormats.DATE_LONGFORMAT)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .featuresToEnable(MapperFeature.USE_GETTERS_AS_SETTERS, MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS).build();

        /** 为objectMapper注册一个带有SerializerModifier的Factory */
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory().withSerializerModifier(new MyBeanSerializerModifier()));

        //SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        //serializerProvider.setNullValueSerializer(NullObjectJsonSerializer.INSTANCE);
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        jackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaTypes.APPLICATION_ACTUATOR2_JSON, MediaTypes.APPLICATION_ACTUATOR3_JSON));
        converters.add(jackson2HttpMessageConverter);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ResourceRegionHttpMessageConverter());
        try {
            converters.add(new SourceHttpMessageConverter<>());
        } catch (Throwable ex) {
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
        if (!CollectionUtils.isEmpty(localResourceProperteis.getLocalLocations())) {
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
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(META_INF_WEBJAR_RESOURCES)
                    .resourceChain(false).addResolver(new WebJarsResourceResolver());
        }

    }

}
