package com.project.smunionbe.domain.notification.basic.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.basic.dto.request.BasicNoticeReqDTO;
import com.project.smunionbe.domain.notification.basic.dto.response.BasicNoticeResDTO;
import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;

import java.util.Map;

public class BasicNoticeConverter {

    public static BasicNotice toBasicNotice(BasicNoticeReqDTO.CreateBasicNoticeRequest request, Club club) {
        return BasicNotice.builder()
                .club(club)
                .title(request.title())
                .content(request.content())
                .target(request.targetDepartments() == null || request.targetDepartments().isEmpty()
                        ? "전체"
                        : String.join(",", request.targetDepartments()))
                .date(request.date())
                .build();
    }

    public static BasicNoticeResDTO.BasicNoticeResponse toBasicNoticeResponse(BasicNotice basicNotice) {
        return new BasicNoticeResDTO.BasicNoticeResponse(
                basicNotice.getId(),
                basicNotice.getTitle(),
                basicNotice.getContent(),
                basicNotice.getTarget(),
                basicNotice.getDate(),
                basicNotice.getCreatedAt()
        );
    }

    public static FCMReqDTO.FCMSendDTO toSendDTO(String fcmToken, BasicNotice basicNotice) {
        return FCMReqDTO.FCMSendDTO.builder()
                .fcmToken(fcmToken)
                .title("📢 새 일반 공지가 등록되었습니다!")
                .body(basicNotice.getTitle() + " - " + basicNotice.getContent())
                .build();
    }
}

