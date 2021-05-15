package com.aqi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableCaching
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
//@ImportResource({"classpath:consumer.xml"})
public class AqiApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.aqi.AqiApplication.class, args);
    }
}
