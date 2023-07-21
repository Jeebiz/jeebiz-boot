package io.hiwepy.boot.demo.setup.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.resource.PathResourceResolver;
import org.springframework.web.reactive.resource.WebJarsResourceResolver;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;

/**
 * https://www.cnblogs.com/niechen/p/9303451.html
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Bean
	public SpringResourceTemplateResolver templateResolver() {

		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}

	@Bean
	public SpringWebFluxTemplateEngine templateEngine() {
		SpringWebFluxTemplateEngine templateEngine = new SpringWebFluxTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		return templateEngine;
	}

	@Bean
	public ThymeleafReactiveViewResolver viewResolver() {
		ThymeleafReactiveViewResolver viewResolver = new ThymeleafReactiveViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	// order matters; cache will find first and render.
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.viewResolver(viewResolver());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
				.resourceChain(false).addResolver(new WebJarsResourceResolver())
				.addResolver(new PathResourceResolver());
	}

	/*
	 * @Bean public WebHandler webHandler(ApplicationContext applicationContext) {
	 * DispatcherHandler dispatcherHandler = new
	 * DispatcherHandler(applicationContext); return dispatcherHandler; }
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
