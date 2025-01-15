package com.project.smunionbe.domain.community.service;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.community.converter.ReplyConverter;
import com.project.smunionbe.domain.community.dto.request.ReplyRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ReplyResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.Reply;
import com.project.smunionbe.domain.community.exception.CommunityErrorCode;
import com.project.smunionbe.domain.community.exception.CommunityException;
import com.project.smunionbe.domain.community.repository.ArticleRepository;
import com.project.smunionbe.domain.community.repository.ReplyRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final ArticleRepository articleRepository;
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberClubRepository memberClubRepository;
    private final ReplyConverter replyConverter;

    @Transactional
    public ReplyResponseDTO.ReplyResponse createReply(ReplyRequestDTO.CreateReplyRequest dto, Long selectedMemberClubId, Long articleId) {
        // MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        Long clubId = memberClub.getClub().getId();
        Long departmentId = memberClub.getDepartment().getId();

        //게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.ARTICLE_NOT_FOUND));

        //동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.CLUB_NOT_FOUND));

        //부서 조회
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.DEPARTMENT_NOT_FOUND));

        //댓글 엔티티 생성
        Reply reply = replyConverter.toReply(dto, article, memberClub);

        replyRepository.save(reply);

        return replyConverter.toReplyResponseDto(reply, club.getName(), department.getName(), memberClub.getNickname());
    }

}
