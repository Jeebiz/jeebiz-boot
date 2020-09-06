/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.biz.web.servlet.handler.Log4j2MDCInterceptor;
import org.springframework.biz.web.servlet.i18n.NestedLocaleResolver;
import org.springframework.biz.web.servlet.theme.NestedThemeResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

import net.jeebiz.boot.autoconfigure.config.LocalResourceProperteis;

@Configuration
@ComponentScan({ "net.jeebiz.**.webmvc", "net.jeebiz.**.web", "net.jeebiz.**.controller" })
@EnableWebMvc
@EnableConfigurationProperties(LocalResourceProperteis.class)
public class DefaultWebMvcConfiguration extends DelegatingWebMvcConfiguration {

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
    	
    	AcceptHeaderLocaleResolver headerLocaleResolver = new AcceptHeaderLocaleResolver() {
    		
    		@Override
    		public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    			super.setLocale(request, response, locale);
    			LocaleContextHolder.setLocale(locale);
    		}
    		
    	};
    	headerLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        resolvers.add(headerLocaleResolver);
        
    	SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver(){
    		
    		@Override
    		public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    			super.setLocale(request, response, locale);
    			LocaleContextHolder.setLocale(locale);
    		}
    		
    	};
    	sessionLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    	resolvers.add(sessionLocaleResolver);
    	
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver(){
    		
    		@Override
    		public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    			super.setLocale(request, response, locale);
    			LocaleContextHolder.setLocale(locale);
    		}
    		
    	};
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

	@Bean
	public Log4j2MDCInterceptor log4j2MDCInterceptor() {
		return new Log4j2MDCInterceptor();
	}
	
	@Bean
	public DefaultWebMvcConfigurer defaultWebMvcConfigurer(LocalResourceProperteis localResourceProperteis,
			ThemeChangeInterceptor themeChangeInterceptor, LocaleChangeInterceptor localeChangeInterceptor,
			Log4j2MDCInterceptor log4j2mdcInterceptor) {
		return new DefaultWebMvcConfigurer(localResourceProperteis, themeChangeInterceptor, localeChangeInterceptor,
				log4j2mdcInterceptor);
	}

}
