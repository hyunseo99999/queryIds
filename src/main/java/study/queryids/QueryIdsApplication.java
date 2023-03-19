package study.queryids;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QueryIdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryIdsApplication.class, args);
    }
}
