package com.huffmancoding.kingdomino;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class KingdominoApplication extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(KingdominoApplication.class);
    }

    public static void main(String[] args)
    {
        SpringApplication application = new SpringApplication(KingdominoApplication.class);
        application.run(args);
    }
}
