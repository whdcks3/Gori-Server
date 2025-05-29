package com.whdcks3.portfolio.gory_server.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        Map<String, Object> props = new HashMap<>();
        System.out.println("HOST: " + dotenv.get("DB_HOST"));

        props.put("spring.datasource.url",
                "jdbc:mysql://" + dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB_NAME"));
        props.put("spring.datasource.username", dotenv.get("DB_USER"));
        props.put("spring.datasource.password", dotenv.get("DB_PASSWORD"));

        props.put("aws.s3.bucket", dotenv.get("AWS_BUCKET"));
        props.put("aws.s3.region", dotenv.get("AWS_REGION"));
        props.put("aws.s3.access-key", dotenv.get("AWS_ACCESS_KEY"));
        props.put("aws.s3.secret-key", dotenv.get("AWS_SECRET_KEY"));

        props.put("server.url", dotenv.get("SERVER_URL"));

        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", props));
    }
}
