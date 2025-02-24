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
import com.project.smunionbe.domain.notification.vote.entity.VoteStatus;
import com.project.smunionbe.domain.notification.vote.exception.VoteErrorCode;
import com.project.smunionbe.domain.notification.vote.exception.VoteException;
import com.project.smunionbe.domain.notification.vote.repository.VoteItemRepository;
import com.project.smunionbe.domain.notification.vote.repository.VoteNoticeRepository;
import com.project.smunionbe.domain.notification.vote.repository.VoteStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteCommandService {

    private final ClubRepository clubRepository;
    private final MemberClubRepository memberClubRepository;
    private final VoteNoticeRepository voteNoticeRepository;
    private final VoteStatusRepository voteStatusRepository;
    private final VoteItemRepository voteItemRepository;
    private final FCMNotificationService fcmNotificationService;

    public void createVoteNotice(VoteReqDTO.CreateVoteDTO request, Long selectedMemberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        if(!memberClub.is_Staff()) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

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

    public void participateVote(Long voteId, VoteReqDTO.ParticipateVoteDTO request, Long selectedMemberClubId) {
        // 1. 투표 공지 조회
        VoteNotice voteNotice = voteNoticeRepository.findById(voteId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.VOTE_NOT_FOUND));

        // 2. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        // 3. 부서 제한 검증
        String target = voteNotice.getTarget();
        if (!"전체".equals(target)) {
            List<String> allowedDepartments = List.of(target.split(", "));
            String memberDepartment = memberClub.getDepartment().getName();
            if (!allowedDepartments.contains(memberDepartment)) {
                throw new VoteException(VoteErrorCode.ACCESS_DENIED);
            }
        }

        // 4. 중복 투표 제한 확인
        if (!voteNotice.isAllowDuplicate()) {
            for (Long optionId : request.voteOptionIds()) {
                // 기존 투표 상태를 확인
                Optional<VoteStatus> existingVote = voteStatusRepository.findByVoteNoticeIdAndVoteItemIdAndMemberClubId(
                        voteNotice.getId(), optionId, memberClub.getId());

                if (existingVote.isPresent()) {
                    // 이미 투표한 항목이 있다면 덮어쓰기 필요 없음 (중복 투표 방지)
                    throw new VoteException(VoteErrorCode.DUPLICATE_VOTE_NOT_ALLOWED);
                }
            }
        }

        // 5. 투표 상태 저장
        for (Long optionId : request.voteOptionIds()) {
            VoteItem voteItem = voteItemRepository.findById(optionId)
                    .orElseThrow(() -> new VoteException(VoteErrorCode.INVALID_VOTE_ITEM));

            // **추가된 검증 로직: VoteItem이 해당 VoteNotice에 속하는지 확인**
            if (!voteItem.getVoteNotice().getId().equals(voteId)) {
                throw new VoteException(VoteErrorCode.INVALID_VOTE_ITEM);
            }

            // 기존 투표 상태를 확인하고 덮어쓰거나 새로 추가
            VoteStatus voteStatus = voteStatusRepository.findByVoteNoticeIdAndVoteItemIdAndMemberClubId(
                            voteNotice.getId(), optionId, memberClub.getId())
                    .orElse(VoteStatus.builder()
                            .voteNotice(voteNotice)
                            .voteItem(voteItem)
                            .memberClub(memberClub)
                            .build());

            voteStatusRepository.save(voteStatus); // 새로 저장하거나 기존 상태를 덮어씁니다
        }
    }

    public void updateVote(Long voteId, VoteReqDTO.UpdateVoteDTO request, Long selectedMemberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        if(!memberClub.is_Staff()) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 2. 투표 공지 조회
        VoteNotice voteNotice = voteNoticeRepository.findById(voteId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.VOTE_NOT_FOUND));

        // 3. 권한 확인
        if (!memberClub.getClub().equals(voteNotice.getClub())) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 4. 투표 항목 업데이트 (기존 항목 삭제 후 새로 생성)
        voteItemRepository.deleteAllByVoteNoticeId(voteNotice.getId());
        List<VoteItem> updatedVoteItems = request.options().stream()
                .map(option -> VoteItem.builder()
                        .voteNotice(voteNotice)
                        .name(option)
                        .build())
                .toList();
        voteItemRepository.saveAll(updatedVoteItems);

        // 5. 투표 공지 정보 업데이트
        voteNotice.update(
                request.title(),
                request.content(),
                request.date(),
                request.allowDuplicate()
        );
    }

    public void deleteVoteNotice(Long voteId, Long selectedMemberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.MEMBER_NOT_FOUND));

        if(!memberClub.is_Staff()) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 2. 투표 공지 조회
        VoteNotice voteNotice = voteNoticeRepository.findById(voteId)
                .orElseThrow(() -> new VoteException(VoteErrorCode.VOTE_NOT_FOUND));

        // 3. 권한 확인
        if (!memberClub.getClub().equals(voteNotice.getClub())) {
            throw new VoteException(VoteErrorCode.ACCESS_DENIED);
        }

        // 4. 관련된 투표 항목 삭제
        voteItemRepository.deleteAllByVoteNoticeId(voteId);

        // 5. 관련된 투표 상태 삭제
        voteStatusRepository.deleteAllByVoteNoticeId(voteId);

        // 6. 투표 공지 삭제
        voteNoticeRepository.delete(voteNotice);
    }
}
