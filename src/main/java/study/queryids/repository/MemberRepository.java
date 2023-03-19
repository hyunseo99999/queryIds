package study.queryids.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.queryids.domain.Member;


public interface MemberRepository extends JpaRepository<Member, Long> {
}
