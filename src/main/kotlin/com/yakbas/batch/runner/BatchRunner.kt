package com.yakbas.batch.runner

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component


@Component
class BatchRunner(private val jobLauncher: JobLauncher, private val job: Job) {

    @EventListener(ApplicationReadyEvent::class) // this will run our job, when it is up and running
    fun runBatch(event: ApplicationReadyEvent) {
        val parameters = JobParametersBuilder()
            .addLong("startedAt", System.currentTimeMillis())
            .toJobParameters()

        jobLauncher.run(job, parameters)
    }
}
