package com.dfusiontech.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-liquibase.properties")
public class LiquibaseConfig {
}
