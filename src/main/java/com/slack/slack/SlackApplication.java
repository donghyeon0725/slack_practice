package com.slack.slack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
public class SlackApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SlackApplication.class);
        app.run(args);
    }

}
