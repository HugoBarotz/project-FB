package com.hugo.project.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseInitialization {

    @PostConstruct
    public void initialization() throws IOException {
        String filePath = "ServiceAccountKey.json";
        File file = new File(filePath);
        System.out.println("File path: " + file.getAbsolutePath());

        FileInputStream serviceAccount = new FileInputStream("ServiceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
