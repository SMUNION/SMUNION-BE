package com.project.smunionbe.domain.notification.fee.service.query;

import com.project.smunionbe.domain.notification.fee.converter.FeeNoticeConverter;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.fee.repository.FeeNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeeQueryService {

    private final FeeNoticeRepository feeNoticeRepository;

    public FeeResDTO.FeeNoticeListResponse getFeeNotices(
            Long clubId, Long cursor, int offset, Long memberClubId) {

        // 1. 통합된 메서드로 데이터 조회
        List<FeeNotice> feeNotices = feeNoticeRepository.findByClubIdWithCursor(
                clubId, cursor, PageRequest.of(0, offset)
        );

        // 2. DTO 변환
        List<FeeResDTO.FeeNoticeResponse> fees = feeNotices.stream()
                .map(FeeNoticeConverter::toFeeNoticeResponse)
                .toList();

        // 3. 다음 페이지 여부 확인
        boolean hasNext = feeNotices.size() == offset;
        Long nextCursor = hasNext ? feeNotices.get(feeNotices.size() - 1).getId() : null;

        // 4. 결과 반환
        return new FeeResDTO.FeeNoticeListResponse(fees, hasNext, nextCursor);
    }
}
