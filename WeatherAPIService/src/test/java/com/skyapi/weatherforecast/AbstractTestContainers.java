package com.skyapi.weatherforecast;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class AbstractTestContainers {

    protected static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("weather-forecast-dao-unit-test")
            .withUsername("weather-forecast")
            .withPassword("password")
            .withReuse(true);


    static {
        mysqlContainer.start();
    }

    @DynamicPropertySource
    private static void registerDataSourceProps(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                mysqlContainer::getJdbcUrl
        );
        registry.add
                ("spring.datasource.username",
                        mysqlContainer::getUsername
                );
        registry.add(
                "spring.datasource.password",
                mysqlContainer::getPassword
        );
    }
}
