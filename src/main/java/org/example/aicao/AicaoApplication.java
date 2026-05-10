package org.example.aicao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.aicao.teaching.mapper")
public class AicaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AicaoApplication.class, args);
    }
}
