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
import org.springframework.biz.web.servlet.i18n.NestedLocaleResolver;
import org.springframework.biz.web.servlet.theme.NestedThemeResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
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
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

import net.jeebiz.boot.autoconfigure.config.LocalResourceProperteis;

//@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "net.jeebiz.**.webmvc", "net.jeebiz.**.web", "net.jeebiz.**.controller" })
@EnableConfigurationProperties(LocalResourceProperteis.class)
public class WebMvcConfiguration implements WebMvcConfigurer {
	   
    /*###########SpringMVC本地化支持###########*/
    
    /* 参考 ： 
		http://blog.csdn.net/wutbiao/article/details/7454345 
		http://yvonxiao.iteye.com/blog/1005183 
	*/
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
    	LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    	localeChangeInterceptor.setParamName("language");
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
        cookieLocaleResolver.setCookieName("language");
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
    
    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(themeChangeInterceptor).addPathPatterns("/theme/change");
		registry.addInterceptor(localeChangeInterceptor).addPathPatterns("/locale/change");
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
	}
	
}
