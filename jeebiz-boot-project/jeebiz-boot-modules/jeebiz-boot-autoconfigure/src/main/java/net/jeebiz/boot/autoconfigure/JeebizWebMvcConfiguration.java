package net.jeebiz.boot.autoconfigure;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.web.servlet.i18n.NestedLocaleResolver;
import org.springframework.biz.web.servlet.theme.NestedThemeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
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

//@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "net.jeebiz.**.webmvc", "net.jeebiz.**.web", "net.jeebiz.**.controller" })
public class JeebizWebMvcConfiguration implements WebMvcConfigurer {

	/*###########SpringMVC文件上传支持###########*/
	/*
	@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver cmr = new CommonsMultipartResolver();
        cmr.setMaxInMemorySize(10240);
        return cmr;
    }
	
	*//**
	 * 文件上传临时路径
	 *//*
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation("/");
        
        //// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;
        //factory.setMaxFileSize("128KB"); //KB,MB
        /// 设置总上传数据总大小
        //factory.setMaxRequestSize("256KB"); 
        //Sets the directory location where files will be stored.
        //factory.setLocation("路径地址");
        
        return factory.createMultipartConfig();
    }*/
    
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
    
    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/*
		 * if (!registry.hasMappingForPattern("/webjars/**")) {
		 * registry.addResourceHandler("/webjars/**").addResourceLocations(
		 * "classpath:/META-INF/resources/webjars/"); } if
		 * (!registry.hasMappingForPattern("/**")) {
		 * 
		 * registry.addResourceHandler("/**").addResourceLocations( RESOURCE_LOCATIONS);
		 * 
		 * }
		 */

		/*
		 * registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/")
		 * .resourceChain(false) .addResolver(new WebJarsResourceResolver())
		 * .addResolver(new PathResourceResolver());
		 */

		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
}
