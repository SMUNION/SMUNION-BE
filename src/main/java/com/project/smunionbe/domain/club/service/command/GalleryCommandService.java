package com.project.smunionbe.domain.club.service.command;

import com.project.smunionbe.domain.club.converter.ClubConverter;
import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.converter.GalleryImagesConverter;
import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.entity.GalleryImages;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.exception.GalleryErrorCode;
import com.project.smunionbe.domain.club.exception.GalleryException;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.GalleryImagesRepository;
import com.project.smunionbe.domain.club.repository.GalleryRepository;
import com.project.smunionbe.domain.community.entity.ArticleImages;
import com.project.smunionbe.domain.community.exception.ImageUploadErrorCode;
import com.project.smunionbe.domain.community.exception.ImageUploadException;
import com.project.smunionbe.domain.community.service.ImageService;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class GalleryCommandService {

    private final GalleryRepository galleryRepository;
    private final ClubRepository clubRepository;
    private final MemberClubRepository memberClubRepository;
    private final ImageService imageService;
    private final GalleryImagesConverter galleryImagesConverter;
    private final GalleryImagesRepository galleryImagesRepository;

    public GalleryResDTO.CreateGalleryResDTO createGallery(GalleryReqDTO.CreateGalleryDTO request, Long memberClubId, List<MultipartFile> images) {

        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));

        Club club = clubRepository.findById(memberClub.getClub().getId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }
        Gallery gallery = GalleryConverter.toGallery(request, club);
        galleryRepository.save(gallery);

        // 이미지가 있는 경우 업로드
        List<String> imageUrls = null;
        if (images != null && !images.isEmpty()) {
            imageUrls = imageService.uploadImages(images);
        }

        //이미지도 articleImagesRepository 에 저장
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String image : imageUrls) {
                GalleryImages galleryImages = galleryImagesConverter.toGalleryImages(gallery, image);
                galleryImagesRepository.save(galleryImages);
            }
        }
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

        // 이미지 조회
        List<String> imageUrls;
        try {
            imageUrls = Optional.ofNullable(galleryImagesRepository.findImageUrlsByGalleryId(galleryId))
                    .orElse(Collections.emptyList());
            log.info("Gallery ID: {}, Retrieved image URLs: {}", galleryId, imageUrls);
        } catch (DataAccessException e) {
            log.error("Failed to retrieve image URLs for galleryId {}: {}", galleryId, e.getMessage(), e);
            throw new ImageUploadException(ImageUploadErrorCode.FILE_READ_ERROR);
        }

        // S3에서 이미지 삭제
        if (!imageUrls.isEmpty()) {
            try {
                if (imageUrls.size() > 1) {
                    imageService.deleteMultipleImagesFromS3(imageUrls);
                } else {
                    imageService.deleteImageFromS3(imageUrls.get(0));
                }
            } catch (Exception e) {
                log.error("S3 이미지 삭제 실패: {}", e.getMessage(), e);
                throw new ImageUploadException(ImageUploadErrorCode.IMAGE_DELETE_FAILED);
            }
        }

        // galleryImages 테이블에서 이미지 삭제
        try {
            galleryImagesRepository.deleteByGalleryId(galleryId);
        } catch (DataAccessException e) {
            log.error("Gallery 이미지 삭제 실패: {}", e.getMessage(), e);
            throw new ImageUploadException(ImageUploadErrorCode.IMAGE_DELETE_FAILED);
        }

        // 갤러리 삭제
        galleryRepository.delete(gallery);


        galleryRepository.delete(gallery);
    }

    public void updateGallery(Long galleryId, GalleryReqDTO.UpdateGalleryRequest request, Long memberClubId, List<MultipartFile> images) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new GalleryException(GalleryErrorCode.GALLERY_NOT_FOUND));
        Long clubId = gallery.getClub().getId();
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        if(!memberClub.is_Staff()) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        gallery.update(request.name());

        //이미지 처리 1. 기존 이미지가 있는 경우 삭제 / 2. 새로운 이미지 업로드

        // 1.
        // 이미지 조회 -> images != null && !images.isEmpty()일 경우에만
        List<String> imageUrls;
        try {
            imageUrls = galleryImagesRepository.findImageUrlsByGalleryId(galleryId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        } catch (DataAccessException e) {
            throw new ImageUploadException(ImageUploadErrorCode.FILE_READ_ERROR);
        }

        // s3에서 이미지 삭제
        if (imageUrls != null && !imageUrls.isEmpty()) {
            if (imageUrls.size() > 1) {
                imageService.deleteMultipleImagesFromS3(imageUrls);
            } else {
                imageService.deleteImageFromS3(imageUrls.get(0));
            }
        }
        // article_images 테이블에서도 이미지 삭제
        try {
            galleryImagesRepository.deleteByGalleryId(galleryId);
        } catch (DataAccessException e) {
            throw new ImageUploadException(ImageUploadErrorCode.IMAGE_DELETE_FAILED);
        }

        // 2.
        // 이미지가 있는 경우 업로드
        imageUrls = Collections.emptyList(); //이미지 삭제에 사용되었던 imageUrls 초기화
        if (images != null && !images.isEmpty()) {
            imageUrls = imageService.uploadImages(images);
        }

        //이미지도 articleImagesRepository 에 저장
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String image : imageUrls) {
                GalleryImages galleryImages = galleryImagesConverter.toGalleryImages(gallery, image);
                galleryImagesRepository.save(galleryImages);
            }
        } else {
            //반환용 imageUrls 초기화
            imageUrls = galleryImagesRepository.findImageUrlsByGalleryId(galleryId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        }
    }
}
