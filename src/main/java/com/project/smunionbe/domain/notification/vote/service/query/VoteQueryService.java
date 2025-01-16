package com.project.smunionbe.domain.notification.vote.service.query;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.vote.dto.response.VoteResDTO;
import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import com.project.smunionbe.domain.notification.vote.entity.VoteStatus;
import com.project.smunionbe.domain.notification.vote.exception.VoteErrorCode;
import com.project.smunionbe.domain.notification.vote.exception.VoteException;
import com.project.smunionbe.domain.notification.vote.repository.VoteItemRepository;
import com.project.smunionbe.domain.notification.vote.repository.VoteNoticeRepository;
import com.project.smunionbe.domain.notification.vote.repository.VoteStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteQueryService {

    private final VoteNoticeRepository voteNoticeRepository;
    private final VoteItemRepository voteItemRepository;
    private final VoteStatusRepository voteStatusRepository;
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

    public VoteResDTO.VoteResultResponse getVoteResults(Long voteId, Long selectedMemberClubId) {
        // 1. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        // 2. 투표 공지 조회
        VoteNotice voteNotice = voteNoticeRepository.findById(voteId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.VOTE_NOT_FOUND));

        // 3. 투표 결과 조회
        List<VoteStatus> statuses = voteStatusRepository.findByVoteNoticeId(voteId);

        // 4. 옵션별 집계
        Map<VoteItem, Long> voteCounts = statuses.stream()
                .collect(Collectors.groupingBy(VoteStatus::getVoteItem, Collectors.counting()));

        // 5. 총 투표 수 계산
        long totalVotes = voteCounts.values().stream().mapToLong(Long::longValue).sum();

        // 6. 응답 생성
        List<VoteResDTO.VoteResult> results = voteCounts.entrySet().stream()
                .map(entry -> {
                    VoteItem voteItem = entry.getKey();
                    long count = entry.getValue();
                    int percentage = (int) ((count * 100.0) / totalVotes);

                    return new VoteResDTO.VoteResult(voteItem.getId(), voteItem.getName(), count, percentage);
                })
                .collect(Collectors.toList());

        return new VoteResDTO.VoteResultResponse(results, voteNotice.isAnonymous());
    }

    public VoteResDTO.VoteAbsenteesResponse getAbsentees(Long voteId, Long selectedMemberClubId) {
        // 1. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        // 2. 투표 공지 조회
        VoteNotice voteNotice = voteNoticeRepository.findById(voteId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.VOTE_NOT_FOUND));

        // 3. 익명 투표 여부 확인
        if (voteNotice.isAnonymous()) {
            throw new VoteException(VoteErrorCode.ANONYMOUS_VOTE_CANNOT_FETCH_ABSENTEES);
        }

        // 4. 권한 검증
        if (!memberClub.getClub().equals(voteNotice.getClub())) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 5. 부서 필터링
        List<MemberClub> targetMembers;
        String target = voteNotice.getTarget(); // 예: "1번 부서, 2번 부서" 또는 "전체"
        if ("전체".equals(target)) {
            targetMembers = memberClubRepository.findAllByClubId(voteNotice.getClub().getId());
        } else {
            List<String> allowedDepartments = List.of(target.split(", "));
            targetMembers = memberClubRepository.findByClubIdAndDepartmentNames(
                    voteNotice.getClub().getId(),
                    allowedDepartments
            );
        }

        // 6. 미참여 멤버 필터링
        List<MemberClub> absentees = targetMembers.stream()
                .filter(member -> !voteStatusRepository.existsByVoteNoticeIdAndMemberClubId(voteId, member.getId()))
                .toList();

        // 7. DTO 변환
        List<VoteResDTO.Absentee> absenteeDTOs = absentees.stream()
                .map(absentee -> new VoteResDTO.Absentee(
                        absentee.getMember().getId(),
                        absentee.getNickname()
                ))
                .toList();

        return new VoteResDTO.VoteAbsenteesResponse(absenteeDTOs);
    }
}
