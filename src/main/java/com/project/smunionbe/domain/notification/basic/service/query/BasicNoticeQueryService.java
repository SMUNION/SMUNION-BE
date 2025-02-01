package com.project.smunionbe.domain.notification.basic.service.query;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.basic.converter.BasicNoticeConverter;
import com.project.smunionbe.domain.notification.basic.dto.response.BasicNoticeResDTO;
import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import com.project.smunionbe.domain.notification.basic.exception.BasicNoticeErrorCode;
import com.project.smunionbe.domain.notification.basic.exception.BasicNoticeException;
import com.project.smunionbe.domain.notification.basic.repository.BasicNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNoticeQueryService {

    private final BasicNoticeRepository basicNoticeRepository;
    private final MemberClubRepository memberClubRepository;

    public BasicNoticeResDTO.BasicNoticeListResponse getBasicNotices(Long cursor, int size, Long memberClubId) {
        // 1. MemberClub 검증 (사용자가 동아리 멤버인지 확인)
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 2. BasicNotice 목록 조회 (커서 기반 페이지네이션)
        List<BasicNotice> basicNotices = basicNoticeRepository.findByClubIdAndCursor(
                memberClub.getClub().getId(),
                cursor,
                PageRequest.of(0, size)
        );

        // 3. 다음 페이지 확인 및 커서 값 설정
        boolean hasNext = basicNotices.size() == size;
        Long nextCursor = hasNext ? basicNotices.get(basicNotices.size() - 1).getId() : null;

        // 4. DTO 변환
        List<BasicNoticeResDTO.BasicNoticeResponse> noticeResponses = basicNotices.stream()
                .map(BasicNoticeConverter::toBasicNoticeResponse)
                .toList();

        // 5. 결과 반환
        return new BasicNoticeResDTO.BasicNoticeListResponse(noticeResponses, hasNext, nextCursor);
    }

    public BasicNoticeResDTO.BasicNoticeDetailResponse getBasicNoticeDetail(Long noticeId, Long memberClubId) {
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        BasicNotice basicNotice = basicNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BasicNoticeException(BasicNoticeErrorCode.NOTICE_NOT_FOUND));

        if (!basicNotice.getClub().getId().equals(memberClub.getClub().getId())) {
            throw new BasicNoticeException(BasicNoticeErrorCode.ACCESS_DENIED);
        }

        return BasicNoticeConverter.toBasicNoticeDetailResponse(basicNotice);
    }
}
