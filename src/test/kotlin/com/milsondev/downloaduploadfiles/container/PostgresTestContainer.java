package com.milsondev.downloaduploadfiles.container;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {
    public static final String IMAGE_VERSION = "postgres:11.1";
    public static final String DATABASE_NAME = "to-do-list";
    public static PostgreSQLContainer container;

    public PostgresTestContainer() {
        super(IMAGE_VERSION);
    }

    @BeforeEach
    public static PostgreSQLContainer getInstance() {
        if (container == null) {
            container = new PostgresTestContainer()
                    .withDatabaseName(DATABASE_NAME)
                    .withInitScript("db.sql")
                    .withEnv("POSTGRES_DB", DATABASE_NAME)
                    .withEnv("POSTGRES_USER", "test")
                    .withEnv("POSTGRES_PASSWORD", "test");
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }

}
