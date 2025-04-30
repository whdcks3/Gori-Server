package com.whdcks3.portfolio.gory_server.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.whdcks3.portfolio.gory_server.firebase.FirebaseProperties;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseConfig {
    private final FirebaseProperties firebaseProperties;

    public FirebaseConfig(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    GoogleCredentials googleCredentials() {
        try {
            if (firebaseProperties.getServiceAccount() != null) {
                try (InputStream is = firebaseProperties.getServiceAccount().getInputStream()) {
                    return GoogleCredentials.fromStream(is);
                }
            } else {
                return GoogleCredentials.getApplicationDefault();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
