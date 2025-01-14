package com.project.smunionbe.domain.notification.vote.service.query;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.vote.dto.response.VoteResDTO;
import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import com.project.smunionbe.domain.notification.vote.exception.VoteErrorCode;
import com.project.smunionbe.domain.notification.vote.exception.VoteException;
import com.project.smunionbe.domain.notification.vote.repository.VoteItemRepository;
import com.project.smunionbe.domain.notification.vote.repository.VoteNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteQueryService {

    private final VoteNoticeRepository voteNoticeRepository;
    private final VoteItemRepository voteItemRepository;
    private final MemberClubRepository memberClubRepository;

    public VoteResDTO.VoteListResponse getVotes(Long cursor, int size, Long selectedMemberClubId) {
        // 1. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        // 2. 동아리 ID 가져오기
        Long clubId = memberClub.getClub().getId();

        // 3. 투표 공지 목록 조회 (커서 기반 페이지네이션)
        Slice<VoteNotice> voteSlice = voteNoticeRepository.findByClubIdAndCursor(
                clubId,
                cursor,
                PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"))
        );

        // 4. 다음 커서 계산
        Long nextCursor = voteSlice.hasNext()
                ? voteSlice.getContent().get(voteSlice.getContent().size() - 1).getId()
                : null;

        // 5. 응답 데이터 생성
        List<VoteResDTO.VoteResponse> votes = voteSlice.getContent().stream()
                .map(vote -> new VoteResDTO.VoteResponse(
                        vote.getId(),
                        vote.getTitle(),
                        vote.getContent(),
                        vote.getDate(),
                        vote.isAllowDuplicate(),
                        vote.isAnonymous(),
                        vote.getCreatedAt()
                ))
                .toList();

        return new VoteResDTO.VoteListResponse(votes, voteSlice.hasNext(), nextCursor);
    }

    public VoteResDTO.VoteDetailResponse getVoteDetail(Long voteId, Long selectedMemberClubId) {
        // 1. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        // 2. 투표 공지 조회
        VoteNotice voteNotice = voteNoticeRepository.findById(voteId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.VOTE_NOT_FOUND));

        // 3. 동아리 권한 확인
        if (!memberClub.getClub().equals(voteNotice.getClub())) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 4. 투표 항목 조회
        List<VoteItem> voteItems = voteItemRepository.findAllByVoteNoticeId(voteId);
        List<VoteResDTO.VoteOptionResponse> options = voteItems.stream()
                .map(item -> new VoteResDTO.VoteOptionResponse(item.getId(), item.getName()))
                .toList();

        return new VoteResDTO.VoteDetailResponse(
                voteNotice.getId(),
                voteNotice.getTitle(),
                voteNotice.getContent(),
                voteNotice.getDate(),
                voteNotice.isAllowDuplicate(),
                voteNotice.isAnonymous(),
                options,
                voteNotice.getCreatedAt()
        );
    }
}
