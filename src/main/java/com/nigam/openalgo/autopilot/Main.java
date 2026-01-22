package com.nigam.openalgo.autopilot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;

//http://localhost:8092
//http://localhost:8092/swagger-ui/index.html
@SpringBootApplication
@EnableCaching
@EnableJms
@ComponentScan(basePackages = {
    "com.nigam.openalgo.autopilot",
    "com.nigam.openalgo.autopilot.api",
    "com.nigam.openalgo.autopilot.ui",
    "com.nigam.openalgo.autopilot.socket",
    "com.nigam.openalgo.autopilot.dblayer",
    "com.nigam.openalgo.autopilot.configuration"
})
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
            logger.info("Starting the application");
            SpringApplication.run(Main.class, args);
    }
}