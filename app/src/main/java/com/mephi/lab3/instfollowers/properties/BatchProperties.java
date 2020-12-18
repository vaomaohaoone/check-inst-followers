package com.mephi.lab3.instfollowers.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки параметра запуска SendLogsRunner
 * */
@ConfigurationProperties(prefix = "batch")
@Data
public class BatchProperties {
    /**
     * Количество считываний
     * */
    private Integer count;
    /**
     * Интервал считывания (миллисекунды)
     * */
    private Long interval;
}
