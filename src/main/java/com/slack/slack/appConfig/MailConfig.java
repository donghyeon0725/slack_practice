package com.slack.slack.appConfig;

import com.slack.slack.appConfig.encoding.Encoding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 메일의 쓰레드 설정을 위한 공간입니다.
 * */
@EnableAsync
@Configuration
public class MailConfig {
    @Value("${spring.mail.host}")
    private String HOST;
    @Value("${spring.mail.port}")
    private Integer PORT;
    @Value("${spring.mail.protocol}")
    private String PROTOCOL;

    @Value("${spring.mail.username}")
    private String USERNAME;

    @Value("${spring.mail.password}")
    private String PASSWORD;



    private static final String MAIL_DEBUG = "mail.debug";
    private static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String MAIL_SMTP_STARTTLS_REQUIRED_BOOLEAN;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String MAIL_SMTP_STARTTLS_ENABLE_BOOLEAN;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String MAIL_SMTP_AUTH_BOOLEAN;


    @Bean(name = "mailSenderExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(30);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setProtocol(PROTOCOL);
        javaMailSender.setPort(PORT);
        javaMailSender.setHost(HOST);
        javaMailSender.setUsername(USERNAME);
        javaMailSender.setPassword(PASSWORD);
        javaMailSender.setDefaultEncoding(Encoding.UTF8.name());

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put(MAIL_SMTP_STARTTLS_REQUIRED, MAIL_SMTP_STARTTLS_REQUIRED_BOOLEAN);
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, MAIL_SMTP_STARTTLS_ENABLE_BOOLEAN);
        properties.put(MAIL_SMTP_AUTH, MAIL_SMTP_AUTH_BOOLEAN);
        properties.put(MAIL_DEBUG, true);
        javaMailSender.setJavaMailProperties(properties);


        return javaMailSender;
    }

}
