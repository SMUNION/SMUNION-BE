package com.project.smunionbe.domain.club.service.command;

import com.project.smunionbe.domain.club.converter.ClubConverter;
import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.exception.GalleryErrorCode;
import com.project.smunionbe.domain.club.exception.GalleryException;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.GalleryRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class GalleryCommandService {

    private final GalleryRepository galleryRepository;
    private final ClubRepository clubRepository;
    private final MemberClubRepository memberClubRepository;

    public GalleryResDTO.CreateGalleryResDTO createGallery(GalleryReqDTO.CreateGalleryDTO request, Long memberClubId) {

        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));

        Club club = clubRepository.findById(memberClub.getClub().getId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }
        Gallery gallery = GalleryConverter.toGallery(request, club);
        galleryRepository.save(gallery);
        return GalleryConverter.toCreateGalleryResponse(gallery);

    }

    public void deleteGallery(Long galleryId, Long memberClubId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new GalleryException(GalleryErrorCode.GALLERY_NOT_FOUND));

        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }


        galleryRepository.delete(gallery);
    }

    public void updateGallery(Long galleryId, GalleryReqDTO.UpdateGalleryRequest request, Long memberClubId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new GalleryException(GalleryErrorCode.GALLERY_NOT_FOUND));
        Long clubId = gallery.getClub().getId();
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }
        // 권한 확인 코드 추가 필요
        /*Long clubId = gallery.getClub().getId();
        if (!memberClubRepository.existsByMemberIdAndClubId(MemberId, clubId)) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/

        gallery.update(request.name(), request.thumbnailUrl());
    }
}
