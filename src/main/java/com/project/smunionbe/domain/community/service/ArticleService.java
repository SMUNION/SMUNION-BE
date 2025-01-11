package com.project.smunionbe.domain.community.service;

import com.project.smunionbe.domain.community.converter.ArticleConverter;
import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.repository.ArticleRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final MemberClubRepository memberClubRepository;
    private final ArticleConverter articleConverter;


    //게시글 생성
    @Transactional
    public ArticleResponseDTO.ArticleResponse createArticle(ArticleRequestDTO.CreateArticleRequest dto, Long selectedMemberClubId) {
        // MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new EntityNotFoundException("MemberClub not found"));

        // 게시글 엔티티 생성
        Article article = articleConverter.toArticle(dto, memberClub);

        // 저장
        articleRepository.save(article);

        // 저장 후 ID 반환
        return articleConverter.toArticleResponseDto(article);
    }
}
