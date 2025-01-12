package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.member.converter.MemberClubConverter;
import com.project.smunionbe.domain.member.dto.response.MemberClubResponseDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberClubService {
    private final MemberClubRepository memberClubRepository;
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberClubConverter memberClubConverter;

    @Transactional(readOnly = true)
    public List<MemberClubResponseDTO.MemberClubResponse> findAllByMemberId(Long memberId) {
        // MemberClub 조회
        List<MemberClub> memberClubs = memberClubRepository.findAllByMemberId(memberId);
        if (memberClubs.isEmpty()) {
            throw new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND);
        }

        // Club 조회 및 매핑
        List<Long> clubIds = memberClubs.stream()
                .map(memberClub -> memberClub.getClub().getId())
                .distinct()
                .toList();

        Map<Long, Club> clubMap = clubRepository.findAllById(clubIds).stream()
                .collect(Collectors.toMap(Club::getId, club -> club));

        for (Long clubId : clubIds) {
            if (!clubMap.containsKey(clubId)) {
                throw new MemberClubException(MemberClubErrorCode.CLUB_NOT_FOUND);
            }
        }

        // Department 조회 및 매핑
        Map<Long, Department> departmentMap = clubMap.values().stream()
                .map(Club::getId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> departmentRepository.findById(id)
                                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.DEPARTMENT_NOT_FOUND))
                ));

        // 변환
        return memberClubConverter.toResponseList(memberClubs, clubMap, departmentMap);
    }
}
