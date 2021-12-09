package com.sparta.showmethecode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ShowMeTheCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShowMeTheCodeApplication.class, args);
    }

}
