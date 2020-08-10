/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.web.servlet.handler.Log4j2MDCInterceptor;
import org.springframework.biz.web.servlet.i18n.NestedLocaleResolver;
import org.springframework.biz.web.servlet.theme.NestedThemeResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

import net.jeebiz.boot.autoconfigure.config.LocalResourceProperteis;

@Configuration
@ComponentScan({ "net.jeebiz.**.webmvc", "net.jeebiz.**.web", "net.jeebiz.**.controller" })
@EnableConfigurationProperties(LocalResourceProperteis.class)
public class DefaultWebMvcConfigurer implements WebMvcConfigurer {
	
	private final String META_INF_RESOURCES = "classpath:/META-INF/resources/"; 
	private final String META_INF_WEBJAR_RESOURCES = "classpath:/META-INF/resources/webjars/"; 
	
	@Bean
	@ConditionalOnMissingBean
	public RequestContextFilter requestContextFilter() {
		RequestContextFilter contextFilter = new RequestContextFilter();
		contextFilter.setThreadContextInheritable(true);
		return contextFilter;
	}
	
    /*########### SpringMVC本地化支持 ###########*/
    
    /* 参考 ： 
		http://blog.csdn.net/wutbiao/article/details/7454345 
		http://yvonxiao.iteye.com/blog/1005183 
	*/
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
    	LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    	localeChangeInterceptor.setParamName("lang");
    	return localeChangeInterceptor;
    }
    
   @Bean
   public LocaleResolver localeResolver() {
    	
    	NestedLocaleResolver nestedLocaleResolver = new NestedLocaleResolver();
    	
    	nestedLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    	
    	List<LocaleResolver> resolvers = new LinkedList<LocaleResolver>();
    	
    	AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver();
    	headerLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        resolvers.add(headerLocaleResolver);
        
    	SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
    	sessionLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    	resolvers.add(sessionLocaleResolver);
    	
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setCookieName("lang");
        cookieLocaleResolver.setCookieMaxAge(-1);
        cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        resolvers.add(cookieLocaleResolver);
        
        
        nestedLocaleResolver.setResolvers(resolvers);
        
        return nestedLocaleResolver;
    }

    /*###########Spring MVC 主题支持########### */
	/*参考 ： http://blog.csdn.net/wutbiao/article/details/7450281 */
	
    @Bean
    public ThemeChangeInterceptor themeChangeInterceptor() {
    	ThemeChangeInterceptor themeChangeInterceptor = new ThemeChangeInterceptor();
    	themeChangeInterceptor.setParamName("theme");
    	return themeChangeInterceptor;
    }
    
    @Bean
    public ResourceBundleThemeSource themeSource() {
    	ResourceBundleThemeSource themeSource = new ResourceBundleThemeSource();
    	themeSource.setBasenamePrefix("classpath:/static/assets/css/themes/");
    	return themeSource;
    }
    
    @Bean
    public ThemeResolver themeResolver() {
    	
    	NestedThemeResolver nestedThemeResolver = new NestedThemeResolver();
    	nestedThemeResolver.setDefaultThemeName("default");
    	
    	List<ThemeResolver> resolvers = new LinkedList<ThemeResolver>();
    	
    	//基于Session的主题解析
    	SessionThemeResolver sessionThemeResolver = new SessionThemeResolver();
    	sessionThemeResolver.setDefaultThemeName("default");
        resolvers.add(sessionThemeResolver);
    	
        //基于Cokie的主题解析
    	CookieThemeResolver cookieThemeResolver = new CookieThemeResolver();
    	cookieThemeResolver.setCookieName("theme");
    	cookieThemeResolver.setDefaultThemeName("default");
    	resolvers.add(cookieThemeResolver);
    	
    	return nestedThemeResolver;
    }
    
    @Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
    
    @Autowired
	private ThemeChangeInterceptor themeChangeInterceptor;
    @Autowired
	private LocaleChangeInterceptor localeChangeInterceptor;
    @Autowired
	private Log4j2MDCInterceptor log4j2MDCInterceptor;
	
	@Bean
	public Log4j2MDCInterceptor log4j2MDCInterceptor() {
		return new Log4j2MDCInterceptor();
	}
	
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(log4j2MDCInterceptor).addPathPatterns("/**").order(Integer.MIN_VALUE);
		registry.addInterceptor(themeChangeInterceptor).addPathPatterns("/**").order(Integer.MIN_VALUE + 1);
		registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/**").order(Integer.MIN_VALUE + 2);
	}

	@Autowired
    private LocalResourceProperteis localResourceProperteis;

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
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		// swagger增加url映射
		if(registry.hasMappingForPattern("/doc.html**")) {
			registry.addResourceHandler("/doc.html**").addResourceLocations(META_INF_RESOURCES);
		}
		if(registry.hasMappingForPattern("/swagger-ui.html**")) {
			registry.addResourceHandler("/swagger-ui.html**").addResourceLocations(META_INF_RESOURCES);
		}
		if(registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations(META_INF_WEBJAR_RESOURCES)
				.resourceChain(false).addResolver(new WebJarsResourceResolver());
		}
		
	}
	
}
