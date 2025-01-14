package com.project.smunionbe.domain.club.service.query;


import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.exception.GalleryErrorCode;
import com.project.smunionbe.domain.club.exception.GalleryException;
import com.project.smunionbe.domain.club.repository.GalleryRepository;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GalleryQueryService {

    private final GalleryRepository galleryRepository;
    private final MemberClubRepository memberClubRepository;

    public GalleryResDTO.GetGalleryResDTO getGallery(Long GalleryId, Long MemberId) {
        // 1. 갤러리 조회 (존재하지 않을 경우 예외 발생)
        Gallery gallery = galleryRepository.findById(GalleryId)
                .orElseThrow(() -> new GalleryException(GalleryErrorCode.GALLERY_NOT_FOUND));

        // 2. 동아리 권한 검증
        /*Long clubId = gallery.getClub().getId();
        if (!memberClubRepository.existsByMemberIdAndClubId(MemberId, clubId)) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/

        return GalleryConverter.toGetGalleryResponse(gallery);
    }


    public GalleryResDTO.GetGalleryListResDTO getAllGallery(
            Long clubId, Long memberId
    ) {
        // 동아리 권한 검증
        // /*Long clubId = gallery.getClub().getId();

        /*if (!memberClubRepository.existsByMemberIdAndClubId(memberId, clubId)) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/


        // 데이터 변환
        List<GalleryResDTO.GetGalleryResDTO> galleryResDTOS = galleryRepository.findAllByClubId(clubId)
                .stream()
                .map(gallery -> new GalleryResDTO.GetGalleryResDTO(
                        gallery.getId(),
                        gallery.getName(),
                        gallery.getThumbnailUrl()
                ))
                .toList();

        return new GalleryResDTO.GetGalleryListResDTO(galleryResDTOS);
    }

}

