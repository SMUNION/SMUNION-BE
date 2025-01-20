package com.project.smunionbe.domain.community.service;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.community.converter.ArticleConverter;
import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.exception.CommunityErrorCode;
import com.project.smunionbe.domain.community.exception.CommunityException;
import com.project.smunionbe.domain.community.repository.ArticleRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberClubRepository memberClubRepository;
    private final ArticleConverter articleConverter;


    //게시글 생성
    @Transactional
    public ArticleResponseDTO.ArticleResponse createArticle(ArticleRequestDTO.CreateArticleRequest dto, Long selectedMemberClubId) {
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
        Article article = articleConverter.toArticle(dto, memberClub, club, department);

        // 저장
        articleRepository.save(article);

        // 저장 후 ID 반환
        return articleConverter.toArticleResponseDto(article, department.getName(), club.getName(), memberClub.getNickname());
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

        // 엔티티 → DTO 변환하여 반환
        return articleConverter.toArticleResponseDto(article, article.getDepartment(), article.getMemberName(), memberClub.getNickname());
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDTO.ArticleResponse> getAllArticles() {
        // 최신순으로 게시글 전체 조회
        List<Article> articles = articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // 엔티티 → DTO 변환
        return articles.stream().map(article -> {
            MemberClub memberClub = memberClubRepository.findById(article.getMemberClub().getId())
                    .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

            return articleConverter.toArticleResponseDto(article, article.getDepartment(), article.getMemberName(), memberClub.getNickname());
        }).toList();
    }
}
