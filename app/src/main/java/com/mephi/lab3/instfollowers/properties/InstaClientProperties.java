package com.mephi.lab3.instfollowers.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки клиента instagram
 * */
@ConfigurationProperties(prefix = "inst")
@Data
public class InstaClientProperties {
    /**
     * Логин
     * */
    private String login;
    /**
     * Пароль
     * */
    private String password;
}
