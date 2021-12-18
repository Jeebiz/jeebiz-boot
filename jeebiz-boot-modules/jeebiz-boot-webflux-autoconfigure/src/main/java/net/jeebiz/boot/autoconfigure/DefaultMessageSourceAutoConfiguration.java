/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.autoconfigure;

import java.time.Duration;
import java.util.List;

import org.springframework.biz.context.NestedMessageSource;
import org.springframework.biz.context.support.MultiResourceBundleMessageSource;
import org.springframework.biz.context.support.ResourceBasenameHandler;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import net.jeebiz.boot.autoconfigure.webflux.I18nResourceBasenameHandler;

@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
@EnableConfigurationProperties
public class DefaultMessageSourceAutoConfiguration {

	private static final Resource[] NO_RESOURCES = {};

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.messages")
	public MessageSourceProperties myMessageSourceProperties() {
		return new MessageSourceProperties();
	}

	@Bean
	public ResourceBasenameHandler resourceBasenameHandler() {
		return new I18nResourceBasenameHandler();
	}

	@Bean
	@Primary
	public MessageSource messageSource(@Qualifier("myMessageSourceProperties") MessageSourceProperties properties, ResourceBasenameHandler resourceBasenameHandler) {
		MultiResourceBundleMessageSource messageSource = new MultiResourceBundleMessageSource();
		messageSource.setBasenameHandler(resourceBasenameHandler);
		if (StringUtils.hasText(properties.getBasename())) {
			messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(
					StringUtils.trimAllWhitespace(properties.getBasename())));
		}
		if (properties.getEncoding() != null) {
			messageSource.setDefaultEncoding(properties.getEncoding().name());
		}
		messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
		Duration cacheDuration = properties.getCacheDuration();
		if (cacheDuration != null) {
			messageSource.setCacheMillis(cacheDuration.toMillis());
		}
		messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
		messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
		return messageSource;
	}

	protected static class ResourceBundleCondition extends SpringBootCondition {

		private static ConcurrentReferenceHashMap<String, ConditionOutcome> cache = new ConcurrentReferenceHashMap<>();

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context,
				AnnotatedTypeMetadata metadata) {
			String basename = context.getEnvironment()
					.getProperty("spring.messages.basename", "messages");
			ConditionOutcome outcome = cache.get(basename);
			if (outcome == null) {
				outcome = getMatchOutcomeForBasename(context, basename);
				cache.put(basename, outcome);
			}
			return outcome;
		}

		private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context,
				String basename) {
			ConditionMessage.Builder message = ConditionMessage
					.forCondition("ResourceBundle");
			for (String name : StringUtils.commaDelimitedListToStringArray(
					StringUtils.trimAllWhitespace(basename))) {
				for (Resource resource : getResources(context.getClassLoader(), name)) {
					if (resource.exists()) {
						return ConditionOutcome
								.match(message.found("bundle").items(resource));
					}
				}
			}
			return ConditionOutcome.noMatch(
					message.didNotFind("bundle with basename " + basename).atAll());
		}

		private Resource[] getResources(ClassLoader classLoader, String name) {
			String target = name.replace('.', '/');
			try {
				return new PathMatchingResourcePatternResolver(classLoader)
						.getResources("classpath*:" + target + ".properties");
			}
			catch (Exception ex) {
				return NO_RESOURCES;
			}
		}

	}

	@Bean
	public NestedMessageSource nestedMessageSource(List<MessageSource> sources) {
		return new NestedMessageSource(sources.toArray(new MessageSource[sources.size()]));
	}




}
