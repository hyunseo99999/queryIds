package study.queryids.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.queryids.dto.MemberSearchCondition;
import study.queryids.dto.MemberTeamDto;
import study.queryids.repository.MemberQueryRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberQueryRepository memberQueryRepository;

    @GetMapping("/v1/members")
    public Page<MemberTeamDto> searchMemberV1(MemberSearchCondition condition, Pageable pageable) {
        return memberQueryRepository.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v2/members")
    public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
        return memberQueryRepository.searchPageComplex(condition, pageable);
    }

}
