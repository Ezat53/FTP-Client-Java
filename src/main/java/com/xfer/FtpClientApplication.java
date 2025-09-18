package com.xfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.xfer")
public class FtpClientApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FtpClientApplication.class);
    }

    public static void main(String[] args) {
        // This method is only called when running as standalone application
        // When deployed to Tomcat, configure() method is used instead
        SpringApplication.run(FtpClientApplication.class, args);
    }
}
