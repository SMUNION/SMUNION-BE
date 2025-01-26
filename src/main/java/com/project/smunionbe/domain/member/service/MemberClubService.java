package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.member.converter.MemberClubConverter;
import com.project.smunionbe.domain.member.dto.request.MemberClubRequestDTO;
import com.project.smunionbe.domain.member.dto.response.MemberClubResponseDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
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
    private final ClubSelectionService clubSelectionService;

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

    @Transactional(readOnly = true)
    public MemberClubResponseDTO.MemberClubResponse findById(Long memberClubId) {
        // MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));
        if (memberClub == null) {
            throw new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND);
        }

        Long clubId = memberClub.getClub().getId();
        Long departmentId = memberClub.getDepartment().getId();

        //동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.CLUB_NOT_FOUND));
        //부서 조회
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.DEPARTMENT_NOT_FOUND));

        // 변환
        return memberClubConverter.toResponse(memberClub, club, department);
    }


    // 선택된 memberClubId 유효성 검사 및 세션에 저장
    @Transactional(readOnly = true)
    public void validateAndSetSelectedProfile(Long memberId, Long memberClubId, HttpSession session) {
        List<MemberClub> memberClubs = memberClubRepository.findAllByMemberId(memberId);

        // 조회된 동아리 중에서 선택한 동아리가 존재하는지 확인
        boolean isValid = memberClubs.stream()
                .anyMatch(memberClub -> memberClub.getId().equals(memberClubId));
        if (!isValid) {
            throw new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB);
        }

        // 세션에 선택된 프로필 저장
        clubSelectionService.setSelectedProfile(session, memberId, memberClubId);
    }

    //동아리 닉네임 변경
    @Transactional
    public MemberClubResponseDTO.MemberClubResponse changeNickname(MemberClubRequestDTO.ChangeNicknameDTO dto, Long memberId, Long memberClubId) {
        //닉네임 입력이 비어있을 경우 예외처리
        if (dto.newNickname() == null || dto.newNickname().isEmpty()) {
            throw new MemberClubException(MemberClubErrorCode.BLANK_NICKNAME);
        }

        // 해당 동아리 가입 정보 조회
        MemberClub memberClub = memberClubRepository.findByIdAndMemberId(memberClubId, memberId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND_MEMBER));

        Long clubId = memberClub.getClub().getId();
        Long departmentId = memberClub.getDepartment().getId();
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.CLUB_NOT_FOUND));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.DEPARTMENT_NOT_FOUND));

        // 닉네임 변경
        memberClub.updateNickname(dto.newNickname());

        // 변경된 닉네임 저장
        memberClubRepository.save(memberClub);

        return memberClubConverter.toResponse(memberClub, club, department);
    }
}
