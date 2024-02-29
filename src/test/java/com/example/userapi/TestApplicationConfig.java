package com.example.userapi;

import com.example.userapi.config.AppConfig;
import org.flywaydb.core.Flyway;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@Configuration
@Profile("test")
public class TestApplicationConfig extends AppConfig {

    @Bean
    public PostgreSQLContainer<?> postgresContainer(){
        PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("userapi_db_test")
                .withUsername("user")
                .withPassword("passwd");
        postgresContainer.start();
        return postgresContainer;
    }

    @Bean
    Mutiny.SessionFactory getMutinySessionFactoryTest(PostgreSQLContainer<?> postgresContainer){

        Map<String, String> prop = new HashMap<>();
        prop.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:" + postgresContainer.getFirstMappedPort() + "/userapi_db_test");
        prop.put("jakarta.persistence.jdbc.user", "user");
        prop.put("jakarta.persistence.jdbc.password", "passwd");
        prop.put("hibernate.show_sql", "true");

        return createEntityManagerFactory("dbSettingsTest", prop).unwrap(Mutiny.SessionFactory.class);
    }

    @Bean
    Flyway getFlyway(PostgreSQLContainer<?> postgresContainer){
        return Flyway.configure().cleanDisabled(false)
                .dataSource("jdbc:postgresql://localhost:" + postgresContainer.getFirstMappedPort()
                        + "/userapi_db_test", "user", "passwd")
                .load();
    }
}
