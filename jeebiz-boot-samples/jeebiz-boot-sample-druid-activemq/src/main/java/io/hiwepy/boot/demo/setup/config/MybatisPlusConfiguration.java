/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.demo.setup.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.dozermapper.core.Mapper;

@Configuration
@MapperScan({"io.hiwepy.**.dao", "io.hiwepy.**repository"})
public class MybatisPlusConfiguration {

	protected static Logger LOG = LoggerFactory.getLogger(MybatisPlusConfiguration.class);

	@Autowired
	private Mapper beanMapper;

	/**
	 * 乐观锁插件
	 */
	@Bean
	public OptimisticLockerInnerInterceptor optimisticLockerInterceptor() {
		return new OptimisticLockerInnerInterceptor();
	}

	/**
	 * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor =
	 * false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
		interceptor.addInnerInterceptor(paginationInterceptor());
		return interceptor;
	}

	/**
	 * mybatis-plus分页插件<br>
	 * 文档：http://mp.baomidou.com<br>
	 */
	@Bean
	public PaginationInnerInterceptor paginationInterceptor() {
		PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
		paginationInterceptor.setOverflow(false);
		return paginationInterceptor;
	}

	@Bean
	public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
		BlockAttackInnerInterceptor sqlExplainInterceptor = new BlockAttackInnerInterceptor();
		return sqlExplainInterceptor;
	}

	/**
	 * 注入sql注入器
	 */
	@Bean
	public ISqlInjector sqlInjector() {
		return new DefaultSqlInjector();
	}

	/**
	 * 注入主键生成器
	@Bean
	public IKeyGenerator keyGenerator() {
		return new H2KeyGenerator();
	}
	*/

	/*
	 * oracle数据库配置JdbcTypeForNull
	 * 参考：https://gitee.com/baomidou/mybatisplus-boot-starter/issues/IHS8X
	 * 不需要这样配置了，参考 yml: mybatis-plus: confuguration dbc-type-for-null: 'null'
	 *
	 * @Bean public ConfigurationCustomizer configurationCustomizer(){ return new
	 * MybatisPlusCustomizers(); }
	 *
	 * class MybatisPlusCustomizers implements ConfigurationCustomizer {
	 *
	 * @Override public void customize(org.apache.ibatis.session.Configuration
	 * configuration) { configuration.setJdbcTypeForNull(JdbcType.NULL); } }
	 */

    public Mapper getBeanMapper() {
		return beanMapper;
	}

}
