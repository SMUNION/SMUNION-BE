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
import com.project.smunionbe.domain.community.service.ImageService;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final ImageService imageService;



    public ClubResDTO.CreateClubDTO createClub(ClubReqDTO.CreateClubDTO request, Long memberId, MultipartFile image) {
        /*if (!memberRepository.existsByEmail((email))) {
            throw new ClubException(ClubErrorCode.MEMBER_NOT_FOUND);
        }*/
        log.info("Request name: {}, description: {}", request.name(), request.description());

        if (clubRepository.existsByName(request.name())) {
            throw new ClubException(ClubErrorCode.CLUB_NAME_ALREADY_EXISTS);
        }
        // ✅ 이미지 저장 후 URL 가져오기 (이미지가 없을 수도 있음)
        String thumbnailImageUrl = (image != null && !image.isEmpty()) ?  imageService.uploadImageToS3(image) : null;

        Club club = ClubConverter.toClub(request,thumbnailImageUrl);
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

    public void updateClub(ClubReqDTO.UpdateClubRequest request, Long memberClubId, MultipartFile image) {
        // 권한 확인 코드 추가 필요
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        Club club = clubRepository.findById(memberClub.getClub().getId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        if (clubRepository.existsByName(request.name())) {
            throw new ClubException(ClubErrorCode.CLUB_NAME_ALREADY_EXISTS);
        }

        String thumbnailUrl = null;

        if (image != null && !image.isEmpty()) {
            // 새로운 이미지가 전달된 경우
            // 기존 동아리 사진 삭제 (기존 사진이 있을 경우)
            if (club.getThumbnailUrl() != null) {
                imageService.deleteImageFromS3(club.getThumbnailUrl());
            }

            // 새 프로필 사진 업로드
            thumbnailUrl = imageService.uploadImageToS3(image);
        } else {
            // 이미지가 null인 경우 썸네일 URL도 null로 처리
            thumbnailUrl = null;

            // 기존 썸네일이 있을 경우 삭제
            if (club.getThumbnailUrl() != null) {
                imageService.deleteImageFromS3(club.getThumbnailUrl());
            }
        }

        // 회원 엔티티에 업데이트
        club.update(request.name(), request.description(), thumbnailUrl);


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
