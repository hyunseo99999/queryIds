package study.queryids;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.queryids.domain.Hello;
import study.queryids.domain.QHello;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class QueryIdsApplicationTests {

    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        QHello qHello = QHello.hello;
        JPAQueryFactory query = new JPAQueryFactory(em);
        Hello result = query.selectFrom(qHello)
                .fetchOne();

        assertThat(result).isEqualTo(hello);
        assertThat(result.getId()).isEqualTo(hello.getId());

    }

}
