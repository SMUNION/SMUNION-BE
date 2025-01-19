package com.project.smunionbe.domain.notification.fee.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;
import com.project.smunionbe.domain.notification.fee.dto.request.FeeReqDTO;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;

public class FeeNoticeConverter {

    public static FeeNotice toFeeNotice(FeeReqDTO.CreateFeeNoticeRequestDTO request, Club club) {

        // 타겟 부서가 없으면 "전체"로 설정
        String target = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? "전체"
                : String.join(", ", request.targetDepartments()); // 부서 이름들을 콤마로 합치기

        return FeeNotice.builder()
                .club(club)
                .title(request.title())
                .content(request.content())
                .amount(request.amount())
                .bank(request.bank())
                .accountNumber(request.accountNumber())
                .target(target)
                .date(request.deadLine())
                .participantCount(request.participantCount())
                .build();
    }

    public static FeeResDTO.FeeNoticeResponse toFeeNoticeResponse(FeeNotice feeNotice) {
        return FeeResDTO.FeeNoticeResponse.builder()
                .feeId(feeNotice.getId())
                .title(feeNotice.getTitle())
                .content(feeNotice.getContent())
                .target(feeNotice.getTarget())
                .amount(feeNotice.getAmount())
                .bank(feeNotice.getBank())
                .accountNumber(feeNotice.getAccountNumber())
                .participantCount(feeNotice.getParticipantCount())
                .deadline(feeNotice.getDate())
                .createdAt(feeNotice.getCreatedAt())
                .build();
    }

    public static FCMReqDTO.FCMSendDTO toSendDTO(String fcmToken, FeeNotice feeNotice) {
        String title = "📢 회비 공지: " + feeNotice.getTitle();
        String content = String.format(
                "💰 %d원 납부 안내\n📝 %s\n🕒 납부 기한: %s",
                feeNotice.getAmount(),
                feeNotice.getContent(),
                feeNotice.getDate().toString()
        );

        return FCMReqDTO.FCMSendDTO.builder()
                .fcmToken(fcmToken)
                .title(title)
                .body(content)
                .build();
    }
}
