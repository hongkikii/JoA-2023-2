package com.mjuAppSW.joA.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {
    private final ClassPathResource firebaseResource = new ClassPathResource("");

    @Bean
    FirebaseApp firebaseApp() throws IOException{
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(firebaseResource.getInputStream()))
            .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException{
        return FirebaseMessaging.getInstance(firebaseApp());
    }
}
