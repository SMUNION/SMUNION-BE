package com.project.smunionbe.domain.notification.basic.service.command;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.basic.converter.BasicNoticeConverter;
import com.project.smunionbe.domain.notification.basic.dto.request.BasicNoticeReqDTO;
import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import com.project.smunionbe.domain.notification.basic.exception.BasicNoticeErrorCode;
import com.project.smunionbe.domain.notification.basic.exception.BasicNoticeException;
import com.project.smunionbe.domain.notification.basic.repository.BasicNoticeRepository;
import com.project.smunionbe.domain.notification.fcm.service.event.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BasicNoticeCommandService {

    private final MemberClubRepository memberClubRepository;
    private final BasicNoticeRepository basicNoticeRepository;
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

        // 4. FCM 푸시 알림 전송
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

        // 3. 새로운 타겟 멤버 조회
        List<MemberClub> newTargetMembers = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? memberClubRepository.findAllByClubId(memberClub.getClub().getId()) // 전체 멤버
                : memberClubRepository.findAllByClubIdAndDepartments(
                memberClub.getClub().getId(),
                request.targetDepartments()
        );

        if (newTargetMembers.isEmpty()) {
            throw new BasicNoticeException(BasicNoticeErrorCode.NO_TARGET_MEMBERS);
        }

        // 4. BasicNotice 수정
        basicNotice.update(
                request.title(),
                request.content(),
                newTargetMembers.isEmpty() ? "전체" : String.join(",", request.targetDepartments()),
                request.date()
        );

        log.info("일반 공지가 수정되었습니다. noticeId: {}, memberClubId: {}", noticeId, memberClubId);
    }
}

