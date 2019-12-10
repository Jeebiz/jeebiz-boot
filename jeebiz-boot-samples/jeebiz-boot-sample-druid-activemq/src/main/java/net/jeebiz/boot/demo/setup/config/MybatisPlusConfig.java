package net.jeebiz.boot.demo.setup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;

@Configuration
public class MybatisPlusConfig {
	
   /**
    * mybatis-plus分页插件<br>
    * 文档：http://mp.baomidou.com<br>
    */
   @Bean
   public PaginationInterceptor paginationInterceptor() {
       PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
       /*
        * 【测试多租户】 SQL 解析处理拦截器<br>
        * 这里固定写成住户 1 实际情况你可以从cookie读取，因此数据看不到 【 麻花藤 】 这条记录（ 注意观察 SQL ）<br>
        
       List<ISqlParser> sqlParserList = new ArrayList<>();
       TenantSqlParser tenantSqlParser = new TenantSqlParser();
       tenantSqlParser.setTenantHandler(new TenantHandler() {

           @Override
           public Expression getTenantId() {
               return new LongValue(1L);
           }

           @Override
           public String getTenantIdColumn() {
               return "tenant_id";
           }

           @Override
           public boolean doTableFilter(String tableName) {
               // 这里可以判断是否过滤表
               if ("user".equals(tableName)) {
                   return true;
               }
               return false;
           }
       });

       sqlParserList.add(tenantSqlParser);
       paginationInterceptor.setSqlParserList(sqlParserList);*/
       
       return paginationInterceptor;
   }

   /**
    * 注入主键生成器
    */
   @Bean
   public IKeyGenerator keyGenerator(){
       return new H2KeyGenerator();
   }

   @Bean
   public SqlExplainInterceptor sqlExplainInterceptor(){
       //启用执行分析插件
       SqlExplainInterceptor sqlExplainInterceptor = new SqlExplainInterceptor();
       return sqlExplainInterceptor;
   }
   
   /*
    * oracle数据库配置JdbcTypeForNull
    * 参考：https://gitee.com/baomidou/mybatisplus-boot-starter/issues/IHS8X
    不需要这样配置了，参考 yml:
    mybatis-plus:
      confuguration
        dbc-type-for-null: 'null' 
   @Bean
   public ConfigurationCustomizer configurationCustomizer(){
       return new MybatisPlusCustomizers();
   }

   class MybatisPlusCustomizers implements ConfigurationCustomizer {

       @Override
       public void customize(org.apache.ibatis.session.Configuration configuration) {
           configuration.setJdbcTypeForNull(JdbcType.NULL);
       }
   }
   */
}