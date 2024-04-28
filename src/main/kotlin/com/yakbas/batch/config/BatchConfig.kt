package com.yakbas.batch.config

import com.yakbas.batch.entity.User
import com.yakbas.batch.repository.UserRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource


@Configuration(proxyBeanMethods = false)
class BatchConfig(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun userItemReader(): RepositoryItemReader<User> {
        return RepositoryItemReaderBuilder<User>()
            .name("user item reader")
            .pageSize(1000)
            .sorts(mapOf("id" to Sort.Direction.ASC))
            .repository(userRepository)
            .methodName("findAll")
            .build()
    }

    @Bean
    fun userItemProcessor() = ItemProcessor<User, User> { item: User ->
        item.copy(fullName = item.firstName + " " + item.lastName)
    }

    @Bean
    fun userItemWriter(): RepositoryItemWriter<User> {
        return RepositoryItemWriterBuilder<User>()
            .repository(userRepository)
            .methodName("save")
            .build()
    }

    @Bean
    fun step(
        reader: RepositoryItemReader<User>,
        processor: ItemProcessor<User, User>,
        writer: RepositoryItemWriter<User>,
        @Qualifier("BatchTaskExecutor") taskExecutor: TaskExecutor
    ): Step {
        return StepBuilder("user item step", jobRepository)
            .chunk<User, User>(1000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .taskExecutor(taskExecutor)
            .build()
    }

    @Bean
    fun job(step: Step): Job {
        return JobBuilder("user item job", jobRepository)
            .start(step)
            .build()
    }

    @Bean
    @Qualifier("BatchTaskExecutor")
    fun taskExecutor(): TaskExecutor {
        return SimpleAsyncTaskExecutor("batch-task").apply {
            this.concurrencyLimit = 10
        }
    }

    @Bean
    fun jdbcClient(dataSource: DataSource) = JdbcClient.create(dataSource)

}