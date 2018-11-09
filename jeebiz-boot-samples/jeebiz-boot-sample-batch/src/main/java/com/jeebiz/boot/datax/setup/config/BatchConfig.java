package com.jeebiz.boot.datax.setup.config;

import java.io.IOException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	 private static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);  
	 
    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Person> flatFileItemReader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        FixedLengthTokenizer fixedLengthTokenizer = new FixedLengthTokenizer();
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"firstName", "lastName"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }
    @Bean
    public JdbcPagingItemReader<Person> jdbcPagingItemReader(DataSource dataSource) {
        JdbcPagingItemReader<Person> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(100);
        reader.setQueryProvider(new MySqlPagingQueryProvider() {{
            setSelectClause("SELECT person_id,first_name,last_name");
            setFromClause("from people");
            setWhereClause("last_name=:lastName");
            setSortKeys(new HashMap<String, Order>() {{
                put("person_id", Order.ASCENDING);
            }});
        }});
        reader.setParameterValues(new HashMap<String, Object>() {{
            put("lastName", "DOE");
        }});
        reader.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        return reader;
    }
    @Bean
    public JdbcBatchItemWriter<Person> jdbcBatchItemWriter(DataSource dataSource) {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
    /*@Bean
    public FlatFileItemWriter<Person> flatFileItemWriter(DataSource dataSource) {
        FlatFileItemWriter<Person> writer = new FlatFileItemWriter<>();
        writer.setAppendAllowed(true);
        writer.setEncoding("UTF-8");
//        writer.set(dataSource);
        return writer;
    }*/
    // end::readerwriterprocessor[]
    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobBuilderFactory jobBuilderFactory, JobCompletionNotificationListener listener, Step step) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step)
                .build();
    }
    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, PersonItemProcessor processor, ItemWriter jdbcBatchItemWriter, ItemReader flatFileItemReader) {
        /*CompositeItemProcessor compositeItemProcessor = new CompositeItemProcessor();
        compositeItemProcessor.setDelegates(Lists.newArrayList(processor, processor));*/
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(flatFileItemReader)
                .processor(processor)
                .writer(jdbcBatchItemWriter)
                .build();
    }
    // end::jobstep[]
    
    
    /** 
     * 读取外部文件方法 
     * @return 
     * @throws IOException  
     */  
    @Bean  
    @StepScope  
    public ItemReader<Object> reader(@Value("#{jobParameters[inputFileBlack]}") String inputFile) throws IOException {   
        logger.info("inputFile:"+new ClassPathResource(inputFile).getURL().getPath());  
        if(inputFile == null){  
            logger.error("The blacklist reader file is null");  
            return null;  
        }  
        FlatFileItemReader<Object> reader = new FlatFileItemReader<Object>();  
         
        reader.setResource(new ClassPathResource(inputFile));  
          
        reader.setLineMapper(lineMapper());  
          
        reader.setLinesToSkip(1);  
          
        //reader.open(JobCompletionNotificationListener.jobExecution.getExecutionContext());  
          
        return reader;  
    }  
      
      
    /** 
     * 读取文本行映射POJO 
     * @return 
     */  
    @Bean  
    @StepScope  
    public LineMapper<Object> lineMapper() {  
        DefaultLineMapper<Object> lineMapper = new DefaultLineMapper<Object>();  
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();  
        lineTokenizer.setDelimiter(",");  
        lineTokenizer.setStrict(false);  
        lineTokenizer.setNames(new String[] { "type","value","fraudType"});  
          
        BeanWrapperFieldSetMapper<Object> fieldSetMapper = new BeanWrapperFieldSetMapper<Object>();  
        fieldSetMapper.setTargetType(Object.class);  
        lineMapper.setLineTokenizer(lineTokenizer);  
        //lineMapper.setFieldSetMapper(new BlackListFieldSetMapper());  
        return lineMapper;  
    }  
  
    /** 
     * 处理过程 
     * @return 
     */  
    @Bean  
    @StepScope  
    public PersonItemProcessor processor(@Value("#{jobParameters[inputFileBlack]}") String inputFile) {  
        return new PersonItemProcessor(inputFile);  
    }  
  
    /** 
     * 写出内容 
     * @return 
     */  
    @Bean  
    @StepScope  
    public ItemWriter<Object> writer() {  
        return new DataOutputItemWriter();  
    }  
  
    /** 
     * 构建job 
     * @param jobs 
     * @param s1  
     * @param listener 
     * @return 
     */  
    @Bean  
    public Job importFileJob(JobBuilderFactory jobs, Step step1,JobExecutionListener listener,JobRepository jobRepository) {  
        return jobs.get("importFileJob")  
                .incrementer(new RunIdIncrementer())  
                .repository(jobRepository)  
                .listener(listener)  
                .flow(step1)  
                .end()  
                .build();  
    }  
      
    /** 
     * 声明step 
     * @param stepBuilderFactory  
     * @param reader  
     * @param writer 
     * @param processor 
     * @return 
     */  
    @Bean  
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Object> reader,  
            ItemWriter<Object> writer, ItemProcessor<Object, Object> processor,PlatformTransactionManager transactionManager) {  
        logger.error("step1");  
        return stepBuilderFactory.get("step1")  
                .<Object, Object> chunk(500)  
                .reader(reader)  
                .processor(processor)  
                .writer(writer)  
                .faultTolerant()  
                .retry(Exception.class)   // 重试  
                .noRetry(ParseException.class)  
                .retryLimit(1)           //每条记录重试一次  
                .listener(new RetryFailuireItemListener())    
                .skip(Exception.class)  
                .skipLimit(500)         //一共允许跳过200次异常  
                .taskExecutor(new SimpleAsyncTaskExecutor()) //设置并发方式执行  
                .throttleLimit(10)        //并发任务数为 10,默认为4  
                .transactionManager(transactionManager)  
                .build();  
    }  
    
    
}
