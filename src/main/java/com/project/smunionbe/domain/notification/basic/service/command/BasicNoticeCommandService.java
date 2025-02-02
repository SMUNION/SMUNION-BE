package com.project.smunionbe.domain.notification.basic.service.command;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.basic.converter.BasicNoticeConverter;
import com.project.smunionbe.domain.notification.basic.dto.request.BasicNoticeReqDTO;
import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import com.project.smunionbe.domain.notification.basic.entity.BasicNoticeStatus;
import com.project.smunionbe.domain.notification.basic.exception.BasicNoticeErrorCode;
import com.project.smunionbe.domain.notification.basic.exception.BasicNoticeException;
import com.project.smunionbe.domain.notification.basic.repository.BasicNoticeRepository;
import com.project.smunionbe.domain.notification.basic.repository.BasicNoticeStatusRepository;
import com.project.smunionbe.domain.notification.fcm.service.event.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BasicNoticeCommandService {

    private final MemberClubRepository memberClubRepository;
    private final BasicNoticeRepository basicNoticeRepository;
    private final BasicNoticeStatusRepository basicNoticeStatusRepository;
    private final FCMNotificationService fcmNotificationService; // FCM 알림 서비스 주입

    public void createBasicNotice(BasicNoticeReqDTO.CreateBasicNoticeRequest request, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        // 2. 타겟 멤버 조회
        List<MemberClub> targetMembers = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? memberClubRepository.findAllByClubId(memberClub.getClub().getId()) // 전체 멤버
                : memberClubRepository.findAllByClubIdAndDepartments(memberClub.getClub().getId(), request.targetDepartments());

        if (targetMembers.isEmpty()) {
            throw new BasicNoticeException(BasicNoticeErrorCode.NO_TARGET_MEMBERS);
        }

        // 3. BasicNotice 생성
        BasicNotice basicNotice = BasicNoticeConverter.toBasicNotice(request, memberClub.getClub());
        basicNoticeRepository.save(basicNotice);

        // 4. 각 멤버에 대한 BasicNoticeStatus 생성
        List<BasicNoticeStatus> statuses = targetMembers.stream()
                .map(member -> BasicNoticeStatus.builder()
                        .basicNotice(basicNotice)
                        .memberClub(member)
                        .isRead(false) // 초기 상태는 '읽지 않음'
                        .build())
                .toList();

        basicNoticeStatusRepository.saveAll(statuses);

        // 5. FCM 푸시 알림 전송
        fcmNotificationService.sendBasicNoticePushNotifications(basicNotice, targetMembers);

        log.info("일반 공지가 생성되었습니다. basicNoticeId: {}, clubId: {}", basicNotice.getId(), memberClub.getClub().getId());
    }

    public void updateBasicNotice(Long noticeId, BasicNoticeReqDTO.UpdateBasicNoticeRequest request, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        // 2. BasicNotice 조회
        BasicNotice basicNotice = basicNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BasicNoticeException(BasicNoticeErrorCode.NOTICE_NOT_FOUND));

        // 권한 확인
        if (!basicNotice.getClub().getId().equals(memberClub.getClub().getId())) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        // 3. 새로운 타겟 멤버 조회
        List<MemberClub> newTargetMembers = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? memberClubRepository.findAllByClubId(basicNotice.getClub().getId()) // 전체 멤버
                : memberClubRepository.findAllByClubIdAndDepartments(basicNotice.getClub().getId(), request.targetDepartments());

        if (newTargetMembers.isEmpty()) {
            throw new BasicNoticeException(BasicNoticeErrorCode.NO_TARGET_MEMBERS);
        }

        // 4. 기존 BasicNoticeStatus 조회
        List<BasicNoticeStatus> existingStatuses = basicNoticeStatusRepository.findAllByBasicNotice(basicNotice);
        Map<Long, BasicNoticeStatus> existingStatusMap = existingStatuses.stream()
                .collect(Collectors.toMap(status -> status.getMemberClub().getId(), status -> status));

        // 5. 상태 업데이트 (기존 상태 갱신 + 새로운 상태 추가)
        for (MemberClub member : newTargetMembers) {
            BasicNoticeStatus status = existingStatusMap.get(member.getId());
            if (status == null) {
                // 새로운 멤버 상태 생성
                basicNoticeStatusRepository.save(BasicNoticeStatus.builder()
                        .basicNotice(basicNotice)
                        .memberClub(member)
                        .isRead(false) // 초기 상태
                        .build());
            }
        }

        // 6. 유효하지 않은 멤버 상태 삭제
        List<BasicNoticeStatus> toRemove = existingStatuses.stream()
                .filter(status -> !newTargetMembers.contains(status.getMemberClub()))
                .toList();
        basicNoticeStatusRepository.deleteAll(toRemove);

        // 7. BasicNotice 수정
        basicNotice.update(
                request.title(),
                request.content(),
                request.targetDepartments() == null || request.targetDepartments().isEmpty() ? "전체" : String.join(", ", request.targetDepartments()),
                request.date()
        );

        log.info("일반 공지가 수정되었습니다. noticeId: {}, memberClubId: {}", noticeId, memberClubId);
    }

    public void markNoticeAsRead(Long noticeId, Long memberClubId) {
        // 1. MemberClub 조회 및 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 2. BasicNotice 조회 및 권한 검증
        BasicNotice basicNotice = basicNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BasicNoticeException(BasicNoticeErrorCode.NOTICE_NOT_FOUND));
        if (!basicNotice.getClub().getId().equals(memberClub.getClub().getId())) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        // 3. 읽음 상태 조회
        BasicNoticeStatus noticeStatus = basicNoticeStatusRepository.findByNoticeAndMemberClub(basicNotice, memberClub)
                .orElseGet(() -> BasicNoticeStatus.builder()
                        .basicNotice(basicNotice)
                        .memberClub(memberClub)
                        .isRead(false)
                        .build());

        // 4. 읽음 상태 업데이트 및 저장
        if (!noticeStatus.isRead()) {
            noticeStatus.markAsRead();
            basicNoticeStatusRepository.save(noticeStatus);
        }

        log.info("공지 읽음 상태가 업데이트되었습니다. noticeId: {}, memberClubId: {}", noticeId, memberClubId);
    }

    public void deleteBasicNotice(Long noticeId, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        // 2. BasicNotice 조회
        BasicNotice basicNotice = basicNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BasicNoticeException(BasicNoticeErrorCode.NOTICE_NOT_FOUND));

        // 권한 확인 (공지의 동아리와 멤버가 속한 동아리 비교)
        if (!basicNotice.getClub().getId().equals(memberClub.getClub().getId())) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        // 3. 연관된 BasicNoticeStatus 삭제
        basicNoticeStatusRepository.deleteByBasicNotice(basicNotice);

        // 4. BasicNotice 삭제
        basicNoticeRepository.delete(basicNotice);

        log.info("일반 공지가 삭제되었습니다. noticeId: {}, memberClubId: {}", noticeId, memberClubId);
    }
}

