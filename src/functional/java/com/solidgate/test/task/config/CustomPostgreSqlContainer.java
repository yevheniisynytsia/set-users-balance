package com.solidgate.test.task.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgreSqlContainer extends PostgreSQLContainer<CustomPostgreSqlContainer> {
    private static final String DB_IMAGE = "postgres:latest";

    private static CustomPostgreSqlContainer postgreSqlContainer;

    private CustomPostgreSqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomPostgreSqlContainer getInstance() {
        if (postgreSqlContainer == null) {
            postgreSqlContainer = new CustomPostgreSqlContainer();
        }
        return postgreSqlContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", postgreSqlContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", postgreSqlContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", postgreSqlContainer.getPassword());
    }

    @Override
    public void stop() {
        super.stop();
    }
}
