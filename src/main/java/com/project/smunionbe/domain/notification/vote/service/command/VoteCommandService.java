package com.project.smunionbe.domain.notification.vote.service.command;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.fcm.service.event.FCMNotificationService;
import com.project.smunionbe.domain.notification.vote.converter.VoteConverter;
import com.project.smunionbe.domain.notification.vote.dto.request.VoteReqDTO;
import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import com.project.smunionbe.domain.notification.vote.exception.VoteErrorCode;
import com.project.smunionbe.domain.notification.vote.exception.VoteException;
import com.project.smunionbe.domain.notification.vote.repository.VoteItemRepository;
import com.project.smunionbe.domain.notification.vote.repository.VoteNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteCommandService {

    private final ClubRepository clubRepository;
    private final MemberClubRepository memberClubRepository;
    private final VoteNoticeRepository voteNoticeRepository;
    private final VoteItemRepository voteItemRepository;
    private final FCMNotificationService fcmNotificationService;

    public void createVoteNotice(VoteReqDTO.CreateVoteDTO request, Long selectedMemberClubId) {
        // 1. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        Long clubId = memberClub.getClub().getId();

        // 2. 권한 확인
        if (!memberClubRepository.existsByMemberIdAndClubId(memberClub.getMember().getId(), clubId)) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 3. 동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.CLUB_NOT_FOUND));

        // 4. 타겟 멤버 조회 (단순 알림용)
        List<MemberClub> targetMembers = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? memberClubRepository.findAllByClubId(clubId) // 전체 부서
                : memberClubRepository.findAllByClubIdAndDepartments(clubId, request.targetDepartments());

        // 5. 투표 공지 생성
        VoteNotice voteNotice = VoteConverter.toVoteNotice(request, club);
        voteNoticeRepository.save(voteNotice);

        // 6. 투표 항목 생성
        List<VoteItem> voteItems = request.options().stream()
                .map(option -> VoteItem.builder()
                        .voteNotice(voteNotice)
                        .name(option)
                        .build())
                .toList();
        voteItemRepository.saveAll(voteItems);

        // 7. FCM 푸시 알림 전송
        fcmNotificationService.sendVotePushNotifications(voteNotice, targetMembers);
    }
}
