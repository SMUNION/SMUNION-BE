package com.project.smunionbe.domain.club.service.command;


import com.project.smunionbe.domain.club.converter.ClubConverter;
import com.project.smunionbe.domain.club.converter.DepartmentConverter;
import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.exception.DepartmentErrorCode;
import com.project.smunionbe.domain.club.exception.DepartmentException;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.member.converter.MemberClubConverter;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.exception.MemberErrorCode;
import com.project.smunionbe.domain.member.exception.MemberException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClubCommandService {
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final RedisUtil redisUtil;
    private final DepartmentRepository departmentRepository;
    private final MemberClubRepository memberClubRepository;



    public ClubResDTO.CreateClubDTO createClub(ClubReqDTO.CreateClubDTO request, Long memberId) {
        /*if (!memberRepository.existsByEmail((email))) {
            throw new ClubException(ClubErrorCode.MEMBER_NOT_FOUND);
        }*/

        if (clubRepository.existsByName(request.name())) {
            throw new ClubException(ClubErrorCode.CLUB_NAME_ALREADY_EXISTS);
        }
        Club club = ClubConverter.toClub(request);
        clubRepository.save(club);

        DepartmentReqDTO.CreateDepartmentDTO createDepartmentDTO = new DepartmentReqDTO.CreateDepartmentDTO("운영진");
        Department department = DepartmentConverter.toDepartment(createDepartmentDTO, club);
        departmentRepository.save(department);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        MemberClub memberClub = MemberClubConverter.toMemberClub(member, club, department, "회장", Boolean.TRUE);
        memberClubRepository.save(memberClub);

        return ClubConverter.toClubResponse(club);

    }

    public void updateClub(ClubReqDTO.UpdateClubRequest request, Long memberClubId) {
        Club club = clubRepository.findById(request.clubId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        // 권한 확인 코드 추가 필요
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        if (clubRepository.existsByName(request.name())) {
            throw new ClubException(ClubErrorCode.CLUB_NAME_ALREADY_EXISTS);
        }


        club.update(request.name(), request.description(), request.thumbnailUrl());
    }

    public void approveClub(ClubReqDTO.ApproveClubRequest request, Long memberId, Long memberClubId) {

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new DepartmentException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND));

        Club club = clubRepository.findById(department.getClub().getId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));


        String key = "Invite_Code:" + request.departmentId();

        if (!redisUtil.hasKey(key)) {
            throw new ClubException(ClubErrorCode.KEY_NOT_FOUND);
        }

        String verifyCode = redisUtil.get(key).toString();

        if (!verifyCode.equals(request.code())) {
            throw new ClubException(ClubErrorCode.CODE_NOT_EQUAL);
        }

        if (memberClubRepository.existsByMemberIdAndClubId(memberId, club.getId())){
            throw new MemberClubException(MemberClubErrorCode.DUPLICATE_MEMBER_CLUB);
        }

        MemberClub memberClub = MemberClubConverter.toMemberClub(member, club, department, request.nickname(), request.isStaff());

        memberClubRepository.save(memberClub);


    }



}
