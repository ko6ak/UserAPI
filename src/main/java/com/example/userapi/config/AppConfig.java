package com.example.userapi.config;

import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

@Configuration
public class AppConfig {

    @Bean
    @Profile("default")
    Mutiny.SessionFactory getMutinySessionFactory(){
        return createEntityManagerFactory("dbSettings").unwrap(Mutiny.SessionFactory.class);
    }
}
