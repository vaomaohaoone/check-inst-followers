package com.mephi.lab3.instfollowers.config;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.mephi.lab3.instfollowers.properties.BatchProperties;
import com.mephi.lab3.instfollowers.properties.InstaClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties(value = {BatchProperties.class, InstaClientProperties.class})
@RequiredArgsConstructor
public class AppConfig {

    private final InstaClientProperties instaClientProperties;

    @Bean
    public IGClient igClient() throws IGLoginException {
        return IGClient
                .builder()
                .username(instaClientProperties.getLogin())
                .password(instaClientProperties.getPassword())
                .login();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("insta-followers");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
