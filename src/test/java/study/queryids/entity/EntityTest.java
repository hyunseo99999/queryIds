package study.queryids.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.queryids.domain.Member;
import study.queryids.domain.QMember;
import study.queryids.domain.QTeam;
import study.queryids.domain.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.queryids.domain.QMember.member;
import static study.queryids.domain.QTeam.team;

@SpringBootTest
@Transactional
public class EntityTest {

    @PersistenceContext
    EntityManager em;

    @Test
    @BeforeEach
    void entityTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamA);
        Member memberC = new Member("memberC", 30, teamB);
        Member memberD = new Member("memberD", 40, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.clear();
        em.flush();

        List<Member> findAll = em.createQuery("select m from Member m", Member.class).getResultList();
        for (Member member : findAll) {
            System.out.println("member -->> " + member);
            System.out.println(" -> team -->" + member.getTeam().getName());
        }
    }

    @Test
    void jpqlTest() {
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "memberA")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("memberA");
    }

    @Test
    void queryIdsTest() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember qMember = member;
        Member findMember = query.selectFrom(qMember)
                .where(qMember.username.eq("memberA"))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("memberA");
    }

    @Test
    void fetchResults() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QueryResults<Member> memberQueryResults = query.selectFrom(member).fetchResults();
        System.out.println(memberQueryResults.getResults()); // contents
        System.out.println(memberQueryResults.getTotal()); // totalCount

        long count = query.selectFrom(member).fetchCount();
        System.out.println(count); // totalCount
    }
    /**
     * 정렬
     * 1. 회원나이 내림차순
     * 2. 회원이름 올림차림
     * 단 2에서 회원이름이 없으면 마지막에 출력(null lists)
     */
    @Test
    void sort() {
        em.persist(new Member(null, 100, null));
        em.persist(new Member("member5", 101, null));
        em.persist(new Member("member6", 102, null));

        JPAQueryFactory query = new JPAQueryFactory(em);

        List<Member> fetch = query.selectFrom(member)
                .orderBy(member.age.desc())
                .orderBy(member.username.asc().nullsLast())
                .fetch();

        assertThat(fetch.get(0).getUsername()).isEqualTo("member6");
        assertThat(fetch.get(1).getUsername()).isEqualTo("member5");
        assertThat(fetch.get(2).getUsername()).isEqualTo(null);
    }

    @Test
    void paging1() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QueryResults<Member> queryResults = query.selectFrom(member)
                .limit(1)
                .offset(2)
                .fetchResults();
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(1);
        assertThat(queryResults.getOffset()).isEqualTo(2);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    @Test
    void aggregation() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        List<Tuple> fetch = query.select(
                        member.age.max()
                        , member.age.sum()
                        , member.age.avg()
                        , member.age.min()
                        , member.age.count()
                )
                .from(member)
                .fetch();

        Tuple tuple = fetch.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
    @Test
    void group() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        List<Tuple> fetch = query.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();
        Tuple teamA = fetch.get(0);
        Tuple teamB = fetch.get(1);
        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);

    }

    @Test
    void join() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        List<Member> teamA = query.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(teamA)
                .extracting("username")
                .containsExactly("memberA", "memberB");
    }

}
