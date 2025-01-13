package com.project.smunionbe.domain.club.service.query;


import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubQueryService {
    private final MemberClubRepository memberClubRepository;
    public ClubResDTO.GetMemberClubListResDTO getAllMemberClubList(
            Long clubId
    ) {


        // 데이터 변환
        List<ClubResDTO.MemberClubResponse> memberClubResponseList = memberClubRepository.findAllByClubId(clubId)
                .stream()
                .map(memberClub -> new ClubResDTO.MemberClubResponse(
                        memberClub.getMember().getId(),
                        memberClub.getNickname(),
                        memberClub.getDepartment().getName()
                ))
                .toList();

        return new ClubResDTO.GetMemberClubListResDTO(memberClubResponseList);
    }
}
