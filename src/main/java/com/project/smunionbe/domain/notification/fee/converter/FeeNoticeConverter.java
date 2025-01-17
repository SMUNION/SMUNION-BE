package com.project.smunionbe.domain.notification.fee.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.fee.dto.request.FeeReqDTO;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;

public class FeeNoticeConverter {

    public static FeeNotice toFeeNotice(FeeReqDTO.CreateFeeNoticeRequestDTO request, Club club) {
        return FeeNotice.builder()
                .club(club)
                .title(request.title())
                .content(request.content())
                .amount(request.amount())
                .bank(request.bank())
                .accountNumber(request.accountNumber())
                .date(request.deadLine())
                .participantCount(request.participantCount())
                .build();
    }
}
