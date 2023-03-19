package study.queryids.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.queryids.domain.Member;
import study.queryids.domain.Team;
import study.queryids.dto.MemberSearchCondition;
import study.queryids.dto.MemberTeamDto;

@SpringBootTest
class MemberRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private MemberQueryRepository memberRepository;

    @Test
    @Transactional
    void simplePage() {
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

        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<MemberTeamDto> results = memberRepository.searchPageSimple(new MemberSearchCondition(), pageRequest);
        for (MemberTeamDto result : results) {
            System.out.println("result --> "+ result.getUsername());
        }


    }

}