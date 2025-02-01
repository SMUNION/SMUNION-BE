package com.project.smunionbe.domain.club.service.query;


import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.exception.GalleryErrorCode;
import com.project.smunionbe.domain.club.exception.GalleryException;
import com.project.smunionbe.domain.club.repository.GalleryImagesRepository;
import com.project.smunionbe.domain.club.repository.GalleryRepository;
import com.project.smunionbe.domain.community.exception.ImageUploadErrorCode;
import com.project.smunionbe.domain.community.exception.ImageUploadException;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GalleryQueryService {

    private final GalleryRepository galleryRepository;
    private final MemberClubRepository memberClubRepository;
    private final GalleryImagesRepository galleryImagesRepository;

    public GalleryResDTO.GetGalleryResDTO getGallery(Long GalleryId, Long memberClubId) {
        // 1. 갤러리 조회 (존재하지 않을 경우 예외 발생)
        Gallery gallery = galleryRepository.findById(GalleryId)
                .orElseThrow(() -> new GalleryException(GalleryErrorCode.GALLERY_NOT_FOUND));

        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        // 저장되어있는 이미지 조회
        List<String> imageUrls;
        try {
            imageUrls = galleryImagesRepository.findImageUrlsByGalleryId(gallery.getId());
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        } catch (DataAccessException e) {
            throw new ImageUploadException(ImageUploadErrorCode.FILE_READ_ERROR);
        }

        return GalleryConverter.toGetGalleryResponse(gallery, imageUrls);
    }


    public GalleryResDTO.GetGalleryListResDTO getAllGallery(
            Long memberClubId
    ) {
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }


        // 데이터 변환
        List<GalleryResDTO.GetGalleryResDTO> galleryResDTOS = galleryRepository.findAllByClubId(memberClub.getClub().getId())
                .stream()
                .map(gallery -> new GalleryResDTO.GetGalleryResDTO(
                        gallery.getId(),
                        gallery.getName(),
                        galleryImagesRepository.findImageUrlsByGalleryId(gallery.getId())
                ))
                .toList();

        return new GalleryResDTO.GetGalleryListResDTO(galleryResDTOS);
    }

}

