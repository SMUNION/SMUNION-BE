package com.project.smunionbe.domain.club.service.command;


import com.project.smunionbe.domain.club.converter.DepartmentConverter;
import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.exception.*;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DepartmentCommandService {
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final RedisUtil redisUtil;
    private final MemberClubRepository memberClubRepository;
    private static final long CODE_EXPIRATION_DAYS = 1L;

    public DepartmentResDTO.CreateDepartmentDTO createDepartment(DepartmentReqDTO.CreateDepartmentDTO request, Long memberClubId) {


        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        Long clubId = memberClubRepository.findById(memberClubId).get().getClub().getId();
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        if (departmentRepository.existsByNameAndClubId(request.name(), clubId)) {
            throw new DepartmentException(DepartmentErrorCode.DEPARTMENT_NAME_ALREADY_EXISTS);
        }
        Department department = DepartmentConverter.toDepartment(request, club);
        departmentRepository.save(department);
        return DepartmentConverter.toCreateDepartmentResponse(department);

    }

    public void deleteDepartment(Long departmentId, Long memberClubId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND));


        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        if (!clubRepository.existsByid(department.getClub().getId())) {
            throw new ClubException(ClubErrorCode.CLUB_NOT_FOUND);
        }


        departmentRepository.delete(department);
    }

    public String createInviteCode(String departmentId, Long memberClubId) {
        Department department = departmentRepository.findById(Long.valueOf(departmentId))
                .orElseThrow(() -> new DepartmentException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND));

        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        if (hasInviteCode(departmentId)) {
            throw new DepartmentException(DepartmentErrorCode.INVITECODE_ALREADY_EXIST);
        }
        String inviteCode = RandomStringUtils.randomAlphanumeric(6);
        log.info("invite code is {}", inviteCode);

        saveInviteCodeInRedis(departmentId, inviteCode);

        return inviteCode;
    }

    // Redis 키 생성 로직
    private String generateRedisKey(String departmentId) {
        return "Invite_Code:" + departmentId;
    }

    // InviteCode 존재 여부 확인
    public boolean hasInviteCode(String departmentId) {
        String redisKey = generateRedisKey(departmentId);
        return redisUtil.hasKey(redisKey);
    }

    // Redis에 Invite Code 저장
    private void saveInviteCodeInRedis(String departmentId, String inviteCode) {
        String redisKey = generateRedisKey(departmentId);
        redisUtil.save(redisKey, inviteCode, CODE_EXPIRATION_DAYS, TimeUnit.DAYS);
        log.info("invite code in redis {}", redisUtil.get(redisKey));

    }
}
