package com.Silver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.Silver.mapper")
@EnableSwagger2
public class SZBlogApplication {

    @Autowired
    DataSource dataSource;
    public static void main(String[] args) {
        SpringApplication.run(SZBlogApplication.class, args);
    }
}
