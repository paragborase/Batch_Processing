package batchjobdemo.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import batchjobdemo.Entity.Customer;
import batchjobdemo.partition.ColumnRangePartitioner;
import batchjobdemo.repository.CustomerRepository;
import lombok.AllArgsConstructor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {
    
    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    //private CustomerRepository customerRepository;

    private CustomerWriter customerWriter;

    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Customer> lineMapper() {
       DefaultLineMapper<Customer> lineMapper=new DefaultLineMapper<>();

       DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
       lineTokenizer.setDelimiter(",");
       lineTokenizer.setStrict(false);
       lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob","credit");

       BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
       fieldSetMapper.setTargetType(Customer.class);

       lineMapper.setLineTokenizer(lineTokenizer);
       lineMapper.setFieldSetMapper(fieldSetMapper);

       return lineMapper;
    }

    @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }

    // @Bean                     //Modified to impliment partition handler
    // public RepositoryItemWriter<Customer> writer(){
    //     RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
    //     writer.setRepository(customerRepository);
    //     writer.setMethodName("save");
    //     return writer;
    // }

    @Bean
    public ColumnRangePartitioner partitioner(){
        return new ColumnRangePartitioner();
    }

    @Bean
    public PartitionHandler partitionHandler(){
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setGridSize(2);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
        taskExecutorPartitionHandler.setStep(slaveStep());
        return taskExecutorPartitionHandler;
    }

    @Bean
    public Step slaveStep(){
        return stepBuilderFactory.get("slaveStep").<Customer,Customer>chunk(500)
                .reader(reader())
                .processor(processor())
                //.writer(writer())
                .writer(customerWriter)
                //.taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step masterStep(){
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep().getName(), partitioner())
                .partitionHandler(partitionHandler())
                .build();
                
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("importCustomers").flow(masterStep()).end().build();
    }


    //By default spring batch execution is in Synchronus format
    //taskexecutor provide process batch in async / concurrent format
    // @Bean 
    // public TaskExecutor taskExecutor() {
    //     SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
    //     asyncTaskExecutor.setConcurrencyLimit(10);
    //     return asyncTaskExecutor;
    // }

    @Bean 
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setQueueCapacity(4);
        taskExecutor.setQueueCapacity(4);
        return taskExecutor();

    }

}
