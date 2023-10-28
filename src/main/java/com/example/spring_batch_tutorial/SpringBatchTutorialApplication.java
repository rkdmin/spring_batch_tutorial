package com.example.spring_batch_tutorial;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing// 배치 작동
@SpringBootApplication
public class SpringBatchTutorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchTutorialApplication.class, args);
    }

}
