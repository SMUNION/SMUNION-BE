package com.project.smunionbe.domain.notification.vote.service.query;

import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.vote.dto.response.VoteResDTO;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import com.project.smunionbe.domain.notification.vote.exception.VoteErrorCode;
import com.project.smunionbe.domain.notification.vote.exception.VoteException;
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
    private final MemberClubRepository memberClubRepository;

    public VoteResDTO.VoteListResponse getVotes(Long clubId, Long cursor, int offset) {
        // 1. 동아리 존재 확인
        if (!memberClubRepository.existsByClubId(clubId)) {
            throw new VoteException(VoteErrorCode.CLUB_NOT_FOUND);
        }

        // 2. 커서 기반 페이징 처리
        Slice<VoteNotice> voteSlice = voteNoticeRepository.findByClubIdAndCursor(
                clubId,
                cursor,
                PageRequest.of(0, offset, Sort.by(Sort.Direction.DESC, "id"))
        );

        // 3. 다음 커서 계산
        Long nextCursor = voteSlice.hasNext()
                ? voteSlice.getContent().get(voteSlice.getContent().size() - 1).getId()
                : null;

        // 4. 데이터 변환
        List<VoteResDTO.VoteResponse> votes = voteSlice.getContent()
                .stream()
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
}
