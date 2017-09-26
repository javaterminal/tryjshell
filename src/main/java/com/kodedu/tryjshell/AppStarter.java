package com.kodedu.tryjshell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class AppStarter {

    public static void main(String[] args) throws IOException {
//        String tmpDir = System.getProperty("java.io.tmpdir");
//        Path prefsPath = Files.createTempDirectory(Paths.get(tmpDir), "prefs");
//        Files.createDirectories(prefsPath);
//        System.setProperty("java.util.prefs.userRoot", prefsPath.normalize().toString());

        SpringApplication.run(AppStarter.class, args);
    }
}
