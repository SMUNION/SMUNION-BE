package com.project.smunionbe.domain.community.service;

import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.Likes;
import com.project.smunionbe.domain.community.exception.CommunityErrorCode;
import com.project.smunionbe.domain.community.exception.CommunityException;
import com.project.smunionbe.domain.community.repository.ArticleRepository;
import com.project.smunionbe.domain.community.repository.LikesRepository;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class LikesService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final LikesRepository likesRepository;

    public boolean toggleLike(Long articleId, Long memberId) {
        // 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.ARTICLE_NOT_FOUND));

        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.MEMBER_NOT_FOUND));


        // 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
        Optional<Likes> existingLike = likesRepository.findByArticleAndMember(article, member);

        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하면 삭제 (좋아요 취소)
            likesRepository.delete(existingLike.get());
            articleRepository.decreaseLikeCount(articleId);
            return false;  // 좋아요 취소
        } else {
            // 좋아요가 없으면 추가
            Likes newLike = Likes.builder()
                    .article(article)
                    .member(member)
                    .build();
            likesRepository.save(newLike);
            articleRepository.increaseLikeCount(articleId);
            return true;  // 좋아요 추가
        }
    }

    public boolean isLiked(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.ARTICLE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.MEMBER_NOT_FOUND));

        return likesRepository.findByArticleAndMember(article, member).isPresent();
    }

}
