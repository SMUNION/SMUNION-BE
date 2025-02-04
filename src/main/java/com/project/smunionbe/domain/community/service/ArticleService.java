package com.project.smunionbe.domain.community.service;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.community.converter.ArticleConverter;
import com.project.smunionbe.domain.community.converter.ArticleImagesConverter;
import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.ArticleImages;
import com.project.smunionbe.domain.community.exception.CommunityErrorCode;
import com.project.smunionbe.domain.community.exception.CommunityException;
import com.project.smunionbe.domain.community.exception.ImageUploadErrorCode;
import com.project.smunionbe.domain.community.exception.ImageUploadException;
import com.project.smunionbe.domain.community.repository.ArticleImagesRepository;
import com.project.smunionbe.domain.community.repository.ArticleRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberClubRepository memberClubRepository;
    private final ArticleConverter articleConverter;
    private final ArticleImagesRepository articleImagesRepository;
    private final ArticleImagesConverter articleImagesConverter;
    private final ImageService imageService;


    //게시글 생성
    @Transactional
    public ArticleResponseDTO.ArticleResponse createArticle(ArticleRequestDTO.CreateArticleRequest request, Long selectedMemberClubId, List<MultipartFile> images) {
        // MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        Long clubId = memberClub.getClub().getId();
        Long departmentId = memberClub.getDepartment().getId();

        //동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.CLUB_NOT_FOUND));
        //부서 조회
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.DEPARTMENT_NOT_FOUND));

        // 게시글 엔티티 생성
        Article article = articleConverter.toArticle(request, memberClub, club, department);

        // 저장
        articleRepository.save(article);

        // 이미지가 있는 경우 업로드
        List<String> imageUrls = null;
        if (images != null && !images.isEmpty()) {
            imageUrls = imageService.uploadImages(images);
        }

        //이미지도 articleImagesRepository 에 저장
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String image : imageUrls) {
                ArticleImages articleImages = articleImagesConverter.toArticleImage(article, image);
                articleImagesRepository.save(articleImages);
            }
        }


        // 저장 후 ID 반환
        return articleConverter.toArticleResponseDto(article, imageUrls, department.getName(), club.getName(), memberClub.getNickname());
    }

    //게시글 단건 조회
    @Transactional(readOnly = true)
    public ArticleResponseDTO.ArticleResponse getArticle(Long articleId) {
        // 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.ARTICLE_NOT_FOUND));

        // MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(article.getMemberClub().getId())
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 저장되어있는 이미지 조회
        List<String> imageUrls;
        try {
            imageUrls = articleImagesRepository.findImageUrlsByArticleId(articleId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        } catch (DataAccessException e) {
            throw new ImageUploadException(ImageUploadErrorCode.FILE_READ_ERROR);
        }

        // 공개 범위에 따라 데이터 필터링
        String clubName = article.getMemberName(); //scope가 0일 경우 개인, 부서, 동아리 전체 표시
        String department = (article.getPublicScope() < 2) ?  article.getDepartment(): "비공개"; //scope가 1일 경우 부서, 동아리 표시
        String memberName = (article.getPublicScope() < 1) ? memberClub.getNickname() : "비공개"; //scope가 2일 경우 동아리 표시

        // 엔티티 → DTO 변환하여 반환
        return articleConverter.toArticleResponseDto(article, imageUrls, clubName, department, memberName);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDTO.ArticleResponse> getAllArticles() {
        // 최신순으로 게시글 전체 조회
        List<Article> articles = articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Long> articleIds = articles.stream().map(Article::getId).toList();

        // 한 번의 쿼리로 여러 게시글의 이미지 URL 조회
        Map<Long, List<String>> articleImageMap = new HashMap<>();
        List<Object[]> imageResults = articleImagesRepository.findImageUrlsByArticleIds(articleIds);

        for (Object[] result : imageResults) {
            Long articleId = (Long) result[0];
            String imageUrl = (String) result[1];

            articleImageMap.computeIfAbsent(articleId, k -> new ArrayList<>()).add(imageUrl);
        }

        // 엔티티 → DTO 변환
        return articles.stream().map(article -> {
            MemberClub memberClub = memberClubRepository.findById(article.getMemberClub().getId())
                    .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

            // 게시글 ID에 해당하는 이미지 리스트 가져오기 (없으면 빈 리스트)
            List<String> imageUrls = articleImageMap.getOrDefault(article.getId(), Collections.emptyList());

            // 공개 범위에 따라 데이터 필터링
            String clubName = article.getMemberName(); //scope가 0일 경우 개인, 부서, 동아리 전체 표시
            String department = (article.getPublicScope() < 2) ?  article.getDepartment(): "비공개"; //scope가 1일 경우 부서, 동아리 표시
            String memberName = (article.getPublicScope() < 1) ? memberClub.getNickname() : "비공개"; //scope가 2일 경우 동아리 표시

            // 엔티티 → DTO 변환하여 반환
            return articleConverter.toArticleResponseDto(article, imageUrls, clubName, department, memberName);
        }).toList();
    }



    @Transactional
    public ArticleResponseDTO.ArticleResponse updateArticle(Long articleId, Long memberClubId, ArticleRequestDTO.UpdateArticleRequest dto, List<MultipartFile> images) {
        // 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.ARTICLE_NOT_FOUND));

        // 수정 권한 확인
        if (!article.getMemberClub().getId().equals(memberClubId)) {
            throw new CommunityException(CommunityErrorCode.UNAUTHORIZED_ACTION);
        }

        // 수정할 필드만 업데이트
        if (dto.title() != null) {
            article.setTitle(dto.title());
        }
        if (dto.content() != null) {
            article.setContent(dto.content());
        }

        Article updatedArticle = articleRepository.save(article);

        //이미지 처리 1. 기존 이미지가 있는 경우 삭제 / 2. 새로운 이미지 업로드

        // 1.
        // 이미지 조회 -> images != null && !images.isEmpty()일 경우에만
        List<String> imageUrls;
        try {
            imageUrls = articleImagesRepository.findImageUrlsByArticleId(articleId);
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
            articleImagesRepository.deleteByArticleId(articleId);
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
                ArticleImages articleImages = articleImagesConverter.toArticleImage(article, image);
                articleImagesRepository.save(articleImages);
            }
        } else {
            //반환용 imageUrls 초기화
            imageUrls = articleImagesRepository.findImageUrlsByArticleId(articleId);
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = Collections.emptyList(); // 빈 리스트로 초기화하여 NullPointerException 방지
            }
        }

        return articleConverter.toArticleResponseDto(updatedArticle, imageUrls, updatedArticle.getDepartment(), updatedArticle.getMemberName(), updatedArticle.getMemberClub().getNickname());
    }


    @Transactional
    public void deleteArticle(Long articleId, Long memberClubId) {
        // 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.ARTICLE_NOT_FOUND));

        // 삭제 권한 확인
        if (!article.getMemberClub().getId().equals(memberClubId)) {
            throw new CommunityException(CommunityErrorCode.UNAUTHORIZED_DELETE_ACTION);
        }

        // 이미지 조회
        List<String> imageUrls;
        try {
            imageUrls = articleImagesRepository.findImageUrlsByArticleId(articleId);
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
            articleImagesRepository.deleteByArticleId(articleId);
        } catch (DataAccessException e) {
            throw new ImageUploadException(ImageUploadErrorCode.IMAGE_DELETE_FAILED);
        }



        // 게시글 삭제
        articleRepository.delete(article);
    }
}
