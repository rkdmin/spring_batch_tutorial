package com.example.spring_batch_tutorial.job.joblistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobLoggerListener implements JobExecutionListener {

    private static final String BEFORE_MESSAGE = "{} Job is Running";
    private static final String AFTER_MESSAGE = "{} Job is Done (Status: {})";

    // Job 실행 되기 전
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(BEFORE_MESSAGE, jobExecution.getJobInstance().getJobName());// job 이름 출력
    }

    // Job 실행 된 후
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(AFTER_MESSAGE,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());// status 출력

        // Job 실패 로그
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("job is failed");
        }
    }

}
