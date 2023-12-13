package com.example.spring_batch_tutorial.job.datareadwrite;


import com.example.spring_batch_tutorial.domain.account.Accounts;
import com.example.spring_batch_tutorial.domain.account.AccountsRepository;
import com.example.spring_batch_tutorial.domain.orders.Orders;
import com.example.spring_batch_tutorial.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

/**
 * desk: 주문 테이블 -> 정산 테이블 이관
 * run: --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class DbDataReadWrite {

    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader<Orders> trOrdersReader,
                                ItemProcessor<Orders, Accounts> trOrderProcessor,
                                ItemWriter<Accounts> trOrdersWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                // .tasklet(helloWorldTasklet()) tesklet 대신 item reader, writer 사용
                .<Orders, Accounts>chunk(5)// 어떤 데이터를 read해서 어떤 데이터를 write할건지, 몇 개의 데이터를 처리할건지(트랜잭션 개수 지정 기존에는 반복문을 작성해야하지만 batch에선 chunk 사용 가능)
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {// 읽어온 데이터를 출력할 수 있는 writer(간단하게)
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProcessor)// 데이터 가공
                .writer(trOrdersWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trOrdersWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")// 저장
                .build();
    }

//    RepositoryItemWriter를 사용하지 않고 ItemWriter로 직접 구현하여 사용할 수 있다.
//    public ItemWriter<Accounts> trOrdersWriter() {
//        return new ItemWriter<Accounts>() {
//            @Override
//            public void write(List<? extends Accounts> items) throws Exception {
//                items.forEach(accountsRepository::save);
//            }
//        };
//    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override// 지금은 간단하게 orders -> Accounts 작업이지만, 실무에선 좀 더 복잡한 Process를 가질 수 있음
            public Accounts process(Orders orders) throws Exception {
                return Accounts.migrationFromOrders(orders);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReaders() {
        return new RepositoryItemReaderBuilder<Orders>()// RepositoryItemReader가 있다는 것을 알면 찾아봐서 변형가능
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)// 보통은 chunksize와 같이 쓰고
                // .arguments()매개변수가 있을 때는 적어준다.
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))// Map 형식으로 오름차순으로 정렬
                .build()
                ;
    }

}
