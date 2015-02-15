package org.hejki.spring.data.jdbc.test;

import org.hejki.spring.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableJdbcRepositories
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);

        NodesJdbcRepository repo = ctx.getBean(NodesJdbcRepository.class);
        Node node = new Node();

        node.setName("Node name");
        node.setAddress("10.0.0.7");
        node.setPort(12321);
        repo.save(node);

        System.out.println(repo.findAll());
    }
}
