package com.project.smunionbe.domain.club.service.command;


import com.project.smunionbe.domain.club.converter.DepartmentConverter;
import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.exception.*;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DepartmentCommandService {
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final RedisUtil redisUtil;
    private static final long CODE_EXPIRATION_DAYS = 1L;

    public DepartmentResDTO.CreateDepartmentDTO createDepartment(DepartmentReqDTO.CreateDepartmentDTO request) {

        /*// 권한 확인 코드 추가 필요
        MemberClub memberClub = memberClubRepository.findByMemberIdAndClubId(memberId, request.clubId());
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/
        Club club = clubRepository.findById(request.clubId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        if (departmentRepository.existsByName(request.name())) {
            throw new DepartmentException(DepartmentErrorCode.DEPARTMENT_NAME_ALREADY_EXISTS);
        }
        Department department = DepartmentConverter.toDepartment(request, club);
        departmentRepository.save(department);
        return DepartmentConverter.toCreateDepartmentResponse(department);

    }

    public void deleteDepartment(Long departmentId, Long memberId) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND));

        /*// 권한 확인 코드 추가 필요
        MemberClub memberClub = memberClubRepository.findByMemberIdAndClubId(memberId, department.getClub().getId());
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/

        if (!clubRepository.existsByid(department.getClub().getId())) {
            throw new ClubException(ClubErrorCode.CLUB_NOT_FOUND);
        }
        /*if (!memberClubRepository.existsByMemberIdAndClubId(memberId, gallery.getClub().getId())) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/

        departmentRepository.delete(department);
    }

    public String createInviteCode(String departmentId) {
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
