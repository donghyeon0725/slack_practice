package com.slack.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(value = {"com.slack.slack.common", "com.slack.admin"})
public class AdminApplication {

    public static void main(String[] args) {
//        ApplicationContext ctx = SpringApplication.run(AdminApplication.class, args);
//        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println("BEAN :: " + beanName);
//        }
        SpringApplication.run(AdminApplication.class, args);
    }

}
