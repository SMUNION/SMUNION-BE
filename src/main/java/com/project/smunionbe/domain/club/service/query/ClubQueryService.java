package com.project.smunionbe.domain.club.service.query;


import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.member.entity.MemberClub;
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
            Long memberClubId
    ) {
        if (!memberClubRepository.existsById(memberClubId)) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        Long clubId = memberClubRepository.findById(memberClubId).get().getClub().getId();

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
